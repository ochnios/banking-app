# BankingApp

**Running**

```shell
git clone https://github.com/ochnios/banking-app.git
cd banking-app
docker compose up -d
```

**After making changes to the frontend**

```shell
cd banking-fe
npm run build
```

**After making changes to the backend**

```shell
cd banking-be
./gradlew build
```

**Built-in users**

```shell
doej3622 : HardPassword123!
smithm1005 : HardPassword123!
```

**Note**
After 3 unsuccessful login attempts, the account is blocked, which can only be unlocked through a bank employee - not implemented yet... :). In this situation, restarting the application can help.
By the way, the frontend is in the alpha testing phase, please be forgiving...
