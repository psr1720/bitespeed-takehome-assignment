# Bitspeed backend assigment

### Deployed Link: https://bitespeed-takehome-assignment-production.up.railway.app

Hit the `/identify` endpoint to test

Request body looks like this:

```json
{
  "emailId": "",
  "phoneNumber": ""
}
```

### Usage instructions.
#### Prerequisites:
* Java version 17 or above
* maven build tool
* local mysql server(can you in memory db otherwise)

**This Project has some local config when used with mysql the application.properties has placeholders for those configs**
* If you are running this locally with mysql then set env variables for the placeholders
* `MYSQLHOST` is the placeholder for the host (will be localhost for local mysql db)
* `MYSQLPORT` is the placeholder for the port (3306 by default for mysql)
* `MYSQLDATABASE` is the placeholder for the database name (ensure that the database already exists as spring is capable of making tables automatically but not entire databases)
* `MYSQLUSERNAME` is the placeholder for the username
* `MYSQLPASSWORD` is the placeholder for the password

These specific names have been chosen as deployment railway would be easier and there won't be a need for a change in the properties profile


