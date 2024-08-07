Simplifications and other notes:
1. address is just a string. in real life it would be a separate entity with street, city, zip, etc.
2. no (re)activation, no deletion of customers
3. using account id instead of a separate account number for now (to save time about the length validation,
and creation)
4. I'm returning the account id in the response, in real life I would probably return nothing, and have another endpoint to list the accounts of a customer.
5. I use customerId for deactivation, because the task said everything's already authenticated,
so I assume the customer is already logged in, and the customerId is available.
6. I have to GETs, for the balance and the transaction history, and they have the accountid in the 
url, of course that would be a security issue, but I'm now "assuming" that this app is a backend service
and the frontend is a separate app  and all this is not actually accessible from outside, so no reason for anyone to issue a GET request with an accountid.
If I have time left, I will create request objects for those too.
7. Actually I changed the GETs to POSTs, which is very much not RESTful, but it's more secure.
I left the GETs commented out, so you can see the original RESTful code, only because this is an interview,
otherwise I don't keep dead code in my projects.
8. I decided in the end to add a jar version so you can run it without gradle.
   resources/temporaryforrunning/teyatinybank.jar probably unnecessary, but why not. 
Some people might use maven only, or I don't know, I just wanted to make sure it's easy to run.

**To run the application:**

You need java 17 and gradle installed.

```
./gradlew bootRun
```
Or 
```
./gradlew bootJar
 
java -jar build/libs/teyatinybank.jar
```
Or open in intellij and run the main class, TeyatinybankApplication


**Create a user**:
```
curl -X POST -H "Content-Type: application/json" -d '{
"firstName": "Peter",
"lastName": "Szilagyi",
"address": "1 PrivateName Street"
}' http://localhost:8080/customers
```

you'll get a customerId and accountId, in a json which you will need for the next steps.

if you try again, it will fail, haven't implemented the 409, it's a general error for the purposes of 
the interview


**Deactivate**:
```
curl -X PUT -H "Content-Type: application/json" -d "customerid eg. 1" http://localhost:8080/customers/deactivate
```

**Deposit**: 

```curl
curl -X POST -H "Content-Type: application/json" -d '{
"accountId": 1,
"amount": 100.0
}' http://localhost:8080/accounts/deposit
```
**Check the balance:**
```
curl -X POST -H "Content-Type: application/json" -d '{
"accountId": 1
}' http://localhost:8080/accounts/balance
```
**Withdraw:**
```
curl -X POST -H "Content-Type: application/json" -d '{
"accountId": 1,
"amount": 50.0
}' http://localhost:8080/accounts/withdraw
```
**Check the transaction history:**
```
curl -X POST -H "Content-Type: application/json" -d '{
"accountId": 1
}' http://localhost:8080/accounts/transactions

```
**Add another user, and you can transfer between them:**
```
 curl -X POST -H "Content-Type: application/json" -d '{
    "firstName": "Meter",
    "lastName": "Milagyi",
    "address": "2 PrivateName Street"
}' http://localhost:8080/customers
```
**Transfer**:

curl -X POST -H "Content-Type: application/json" -d '{
"fromAccountId": 1,
"toAccountId": 2,
"amount": 50.0
}' http://localhost:8080/accounts/transfer

```
