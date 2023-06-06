<h1 align="center">Android Transaction Parser</h1></br>
<p align="center">
Android Library to extract sms from Bank/Credit Card provider into transaction data
</p>
<br>

## Including in your project
Add it in your root build.gradle at the end of repositories on your gradle file:
```Gradle
allprojects {
	repositories {
	...
	maven { url 'https://jitpack.io' }
	}
}
```

Next, add the dependency below to your **module**'s `build.gradle` file:
```gradle
dependencies {
	implementation 'com.github.andrea-liu87:transaction-parser:<version-name>'
}
```

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
Fetch last 12 sms content related
```
parser.fetchLastSms(this, arrayListOf("Liv"), 12)
```
Fetch last 3 months content related
```
parser.fetchLastMonthsSms(this, arrayListOf("Liv"), 3)
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
 <br>

 Fetch all transaction
```
parser.fetchAllTransactions(
                this@MainActivity,
                arrayListOf("Liv"),
                {homeViewModel.updateTransactions(it)})
```
Fetch last 12 transactions
```
parser.fetchLastTransactions(
                this@MainActivity,
                arrayListOf("Liv"),
                {homeViewModel.updateTransactions(it)},
            12)
```
Fetch last 3 months transactions
```
parser.fetchTransactionsLastMonthsOf(
                this@MainActivity,
                arrayListOf("Liv"),
                {homeViewModel.updateTransactions(it)},
                3)
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
