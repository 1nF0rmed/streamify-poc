import com.hedera.hashgraph.sdk.*;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.concurrent.TimeoutException;

public class SetupServer {
    public static void mainy(String[] args) throws TimeoutException, PrecheckStatusException, ReceiptStatusException {

        AccountId accountId = AccountId.fromString(Dotenv.load().get("MY_ACCOUNT_ID"));
        PrivateKey privateKey = PrivateKey.fromString(Dotenv.load().get("MY_PRIVATE_KEY"));

        Client client = Client.forTestnet();
        client.setOperator(accountId, privateKey);

        PrivateKey newAccountPK = PrivateKey.generate();
        PublicKey newPublicKey = newAccountPK.getPublicKey();

        TransactionResponse newAccount = new AccountCreateTransaction()
                .setKey(newAccountPK)
                .setInitialBalance(Hbar.fromTinybars(100000))
                .execute(client);

        AccountId newAccountId = newAccount.getReceipt(client).accountId;
        System.out.println("The new account ID is: "+newAccountId);

        assert newAccountId != null;

//        TransactionResponse sendHbar = new TransferTransaction()
//                .addHbarTransfer(accountId, Hbar.fromTinybars(-1000))
//                .addHbarTransfer(newAccountId, Hbar.fromTinybars(1000))
//                .execute(client);
//
//        System.out.println("The transfer transaction was: " +sendHbar.getReceipt(client).status);

        Hbar queryCost = new AccountBalanceQuery()
                .setAccountId(newAccountId)
                .getCost(client);

        System.out.println("The cost of this query is: " +queryCost);

        AccountBalance accountBalanceNew = new AccountBalanceQuery()
                .setAccountId(newAccountId)
                .execute(client);

        System.out.println("The new account balance is: " +accountBalanceNew.hbars);
    }
}
