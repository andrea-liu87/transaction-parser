<h1 align="center">Android Transaction Parser</h1></br>
<p align="center">
Android Library to extract sms from Bank/Credit Card provider into transaction data
</p>
<br>

## Usage
Check permission inside activity before usage
```
val parser = TransactionParser()
parser.checkPermission(this@MainActivity)
```

Fetch all sms content related
```
parser.fetchAllSMS(this@MainActivity, arrayListOf("DBS"))
```
The return of fetchSms()
```
data class SMS (
    val id:Int,
    val sender: String,
    val content: String,
    val date: String
)
 ```

The return for fetchTransactions()
```
data class Transaction (
    val id:Int,
    val amount: Double,
    val currency: Currency,
    val transactionType: TransactionType,
    val date: String,
    val source: String,
    val merchant: String
)
```
