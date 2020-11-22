package lab2;

import java.util.ArrayList;

public class ClientDatabase {
    static Account account1 = new Account(000001, 1111, 5000);
    static Account account2 = new Account(000002, 2222, 7000);
    static Account account3 = new Account(000003, 3333, 3000);
    static Account account4 = new Account(000004, 4444, 10000);
    static Account account5 = new Account(000005, 5555, 2000);

    static ArrayList<Account> accounts = new ArrayList<>();

    static {
        accounts.add(account1);
        accounts.add(account2);
        accounts.add(account3);
        accounts.add(account4);
        accounts.add(account5);
    }
}
