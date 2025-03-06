import java.sql.*;
import java.util.Scanner;

class BankAccount {
    private String accountHolder;
    private double balance;
    private Connection conn;

    public BankAccount(String accountHolder, double initialBalance, Connection conn) {
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
        this.conn = conn;
        createAccount();
    }

    private void createAccount() {
        try {
            String sql = "INSERT INTO accounts (name, balance) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, accountHolder);
            pstmt.setDouble(2, balance);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            updateBalance();
            System.out.println("Deposited: " + amount);
        } else {
            System.out.println("Invalid amount.");
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            updateBalance();
            System.out.println("Withdrawn: " + amount);
        } else {
            System.out.println("Insufficient funds or invalid amount.");
        }
    }

    private void updateBalance() {
        try {
            String sql = "UPDATE accounts SET balance = ? WHERE name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, balance);
            pstmt.setString(2, accountHolder);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayBalance() {
        try {
            String sql = "SELECT balance FROM accounts WHERE name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, accountHolder);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("Account Holder: " + accountHolder);
                System.out.println("Current Balance: " + rs.getDouble("balance"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

public class BankingSystem {
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:banking.db")) {
            createTable(conn);
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter Account Holder Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Initial Balance: ");
            double initialBalance = scanner.nextDouble();

            BankAccount account = new BankAccount(name, initialBalance, conn);

            while (true) {
                System.out.println("\n1. Deposit\n2. Withdraw\n3. Check Balance\n4. Exit");
                System.out.print("Enter choice: ");
                int choice = scanner.nextInt();

                if (choice == 1) {
                    System.out.print("Enter amount to deposit: ");
                    double amount = scanner.nextDouble();
                    account.deposit(amount);
                } else if (choice == 2) {
                    System.out.print("Enter amount to withdraw: ");
                    double amount = scanner.nextDouble();
                    account.withdraw(amount);
                } else if (choice == 3) {
                    account.displayBalance();
                } else if (choice == 4) {
                    System.out.println("Exiting...");
                    break;
                } else {
                    System.out.println("Invalid choice.");
                }
            }
            scanner.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTable(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS accounts (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "name TEXT UNIQUE, " +
                         "balance REAL)";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
