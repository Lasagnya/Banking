package com.project.banking.util;

import com.project.banking.models.TransactionIncoming;

public interface TransactionVerification {
	int verify(TransactionIncoming transaction);
}
