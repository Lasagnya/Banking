package com.project.banking.util;

import com.project.banking.enumeration.Period;
import com.project.banking.enumeration.TypeOfTransaction;
import com.project.banking.domain.Account;
import com.project.banking.domain.Transaction;
import com.project.banking.service.TransactionService;
import com.project.banking.service.UserService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Component
public class DocumentsFunctionality {
	private final TransactionService transactionService;
	private final UserService userService;

	@Autowired
	public DocumentsFunctionality(TransactionService transactionService, UserService userService) {
		this.transactionService = transactionService;
		this.userService = userService;
	}

	/**
	 * Создание выписки по счёту в txt файл
	 * @param account по этому счёту осуществляется выписка
	 * @param period выписка по этому периоду
	 */
	public void excerpt(Account account, Period period) {
		List<Transaction> transactions = transactionService.getTransactionsByAccountForPeriod(account, period);

		try {
			Files.createDirectories(Path.of("excerpt"));
			FileWriter fw = new FileWriter(String.format("excerpt/excerpt%d.txt", account.getId()));
			BufferedWriter bw = new BufferedWriter(fw);
			String title = "Выписка";
			String output = String.format("%30s%30s\n", "#", "").replace("#", title);
			bw.write(output);
			output = String.format("%28s%28s\n", "#", "").replace("#", "Clever-Bank");
			bw.write(output);
			bw.write(String.format(" %-26s| %-37s\n", "Клиент", userService.getUser().getName()));
			bw.write(String.format(" %-26s| %-37s\n", "Счёт", account.getId()));
			bw.write(String.format(" %-26s| %-37s\n", "Валюта", account.getCurrency().toString()));
			DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy");
			DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyy, HH:mm");
			bw.write(String.format(" %-26s| %-37s\n", "Дата открытия", dateFormat.format(account.getOpening().getTime())));
			if (period == Period.MONTH) {
				Date startDate = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
				bw.write(String.format(" %-26s| %-37s\n", "Период выписки", dateFormat.format(startDate) + " - " + dateFormat.format(new Date())));
			}
			if (period == Period.YEAR) {
				Date startDate = Date.from(LocalDate.now().minusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
				bw.write(String.format(" %-26s| %-37s\n", "Период выписки", dateFormat.format(startDate) + " - " + dateFormat.format(new Date())));
			}
			if (period == Period.ALL)
				bw.write(String.format(" %-26s| %-37s\n", "Период выписки", "За всё время"));
			bw.write(String.format(" %-26s| %-37s\n", "Дата и время формирования", timeFormat.format(new Date().getTime())));
			bw.write(String.format(" %-26s| %-37s\n", "Остаток", account.getBalance()));
			bw.write(String.format(" %-12s| %-33s | %-15s\n", "   Дата", "           Примечание", "    Сумма"));
			bw.write(String.format("%66s\n", "").replace(" ", "-"));
			for (Transaction transaction : transactions) {
				String amount = transaction.getAmount() + " " + transaction.getCurrency().toString();
				if (transaction.getTypeOfTransaction().equals(TypeOfTransaction.WITHDRAWAL) ||
						(transaction.getTypeOfTransaction().equals(TypeOfTransaction.TRANSFER) &&
								(transaction.getReceivingAccount() != account.getId() || transaction.getReceivingBank() != account.getBank().getId()))) {
					amount = "-" + amount;
				}
				bw.write(String.format(" %-12s| %-33s | %-15s\n",
						dateFormat.format(transaction.getTime().getTime()),
						transaction.getTypeOfTransaction().getTitle(),
						amount));
			}
			bw.close();
			fw.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Создание выписки по счёту в pdf файл
	 * @param account по этому счёту осуществляется выписка
	 * @param period выписка по этому периоду
	 */
	public void excerptInPDF(Account account, Period period) {
		List<Transaction> transactions = transactionService.getTransactionsByAccountForPeriod(account, period);

		try {
			Files.createDirectories(Path.of("excerpt"));
			PDDocument document = new PDDocument();
			PDPage page = new PDPage();
			document.addPage(page);

			PDPageContentStream contentStream = new PDPageContentStream(document, page);

			PDFont font = PDType0Font.load(document, new File("fonts/CascadiaMono-SemiLight.ttf"));
			contentStream.beginText();
			contentStream.setFont(font, 12);
			contentStream.setLeading(14.5f);
			contentStream.newLineAtOffset(25, 750);
			String title = "Выписка";
			String output = String.format("%30s%30s", "#", "").replace("#", title);
			contentStream.showText(output);
			contentStream.newLine();
			output = String.format("%28s%28s", "#", "").replace("#", "Clever-Bank");
			contentStream.showText(output);
			contentStream.newLine();
			contentStream.showText(String.format(" %-26s| %-37s", "Клиент", userService.getUser().getName()));
			contentStream.newLine();
			contentStream.showText(String.format(" %-26s| %-37s", "Счёт", account.getId()));
			contentStream.newLine();
			contentStream.showText(String.format(" %-26s| %-37s", "Валюта", account.getCurrency().toString()));
			contentStream.newLine();
			DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy");
			DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyy, HH:mm");
			contentStream.showText(String.format(" %-26s| %-37s", "Дата открытия", dateFormat.format(account.getOpening().getTime())));
			contentStream.newLine();
			if (period == Period.MONTH) {
				Date startDate = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
				contentStream.showText(String.format(" %-26s| %-37s", "Период выписки", dateFormat.format(startDate) + " - " + dateFormat.format(new Date())));
			}
			if (period == Period.YEAR) {
				Date startDate = Date.from(LocalDate.now().minusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
				contentStream.showText(String.format(" %-26s| %-37s", "Период выписки", dateFormat.format(startDate) + " - " + dateFormat.format(new Date())));
			}
			if (period == Period.ALL)
				contentStream.showText(String.format(" %-26s| %-37s", "Период выписки", "За всё время"));
			contentStream.newLine();
			contentStream.showText(String.format(" %-26s| %-37s", "Дата и время формирования", timeFormat.format(new Date().getTime())));
			contentStream.newLine();
			contentStream.showText(String.format(" %-26s| %-37s", "Остаток", account.getBalance()));
			contentStream.newLine();
			contentStream.showText(String.format(" %-12s| %-33s | %-15s", "   Дата", "           Примечание", "    Сумма"));
			contentStream.newLine();
			contentStream.showText(String.format("%66s", "").replace(" ", "-"));
			contentStream.newLine();
			int i = 1;
			for (Transaction transaction : transactions) {
				if (i == 41) {
					contentStream.endText();
					contentStream.close();
					page = new PDPage();
					document.addPage(page);
					contentStream = new PDPageContentStream(document, page);
					font = PDType0Font.load(document, new File("fonts/CascadiaMono-SemiLight.ttf"));
					contentStream.beginText();
					contentStream.setFont(font, 12);
					contentStream.setLeading(14.5f);
					contentStream.newLineAtOffset(25, 750);
					i = 1;
				}

				String amount = transaction.getAmount() + " " + transaction.getCurrency().toString();
				if (transaction.getTypeOfTransaction().equals(TypeOfTransaction.WITHDRAWAL) ||
						(transaction.getTypeOfTransaction().equals(TypeOfTransaction.TRANSFER) &&
								(transaction.getReceivingAccount() != account.getId() || transaction.getReceivingBank() != account.getBank().getId()))) {
					amount = "-" + amount;
				}
				contentStream.showText(String.format(" %-12s| %-33s | %-15s",
						dateFormat.format(transaction.getTime().getTime()),
						transaction.getTypeOfTransaction().getTitle(),
						amount));
				contentStream.newLine();
				i++;
			}
			contentStream.endText();
			contentStream.close();

			document.save(String.format("excerpt/excerpt%d.pdf", account.getId()));
			document.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
