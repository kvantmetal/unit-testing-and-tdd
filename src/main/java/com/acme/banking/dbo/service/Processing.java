package com.acme.banking.dbo.service;

import com.acme.banking.dbo.dao.AccountDao;
import com.acme.banking.dbo.dao.ClientDao;
import com.acme.banking.dbo.domain.Account;
import com.acme.banking.dbo.domain.Cash;
import com.acme.banking.dbo.domain.Client;

import java.util.Collection;

public class Processing {
    private final ClientDao clientDao;
    private final AccountDao accountDao;

    public Processing(ClientDao clientDao, AccountDao accountDao) {
        this.clientDao = clientDao;
        this.accountDao = accountDao;
    }

    public Client createClient(String name) {
        return clientDao.saveClient(name);
    }

    public Collection<Account> getAccountsByClientId(int clientId) {
        Client client = getClient(clientId);
        Collection<Account> accounts = client.getAccounts();
        if (accounts == null || accounts.isEmpty()){
            throw new IllegalArgumentException("Client has not Accounts");
        }
        return accounts;
    }

    public void transfer(int fromAccountId, int toAccountId, double amount) {
        Account accountTransfer = getAccountTransfer(fromAccountId);
        Account accountReceive = getAccountTransfer(toAccountId);

        accountTransfer.minusCash(amount);
        accountReceive.plusCash(amount);

        accountDao.updateAmount(accountTransfer);
        accountDao.updateAmount(accountReceive);

    }

    private Account getAccountTransfer(int accountId) {
        Account account = accountDao.selectById(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found by clientId=" + accountId);
        }
        return account;
    }

    private Client getClient(int clientId) {
        Client client = clientDao.selectClientById(clientId);
        if (client == null) {
            throw new IllegalArgumentException("Client not found by clientId=" + clientId);
        }
        return client;
    }

    public void cash(double amount, int fromAccountId) {
        Cash.log(amount, fromAccountId);
    }
}
