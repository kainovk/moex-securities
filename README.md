# Moex securities

## Prerequisites

Create empty database and describe configuration in [this file](src/main/resources/application.conf).
Flyway will apply migrations to your database after starting the program.

## Application endpoints

### *Security*

1. Create security \
   `POST /securities`

```json
{
  "secid": "AEDRUB_TOD",
  "regnumber": "",
  "name": "AED/RUB_TOD - AED/РУБ", // nullable
  "emitentTitle": ""
}
```

2. Get security by id \
   `GET /securities/{id}`

3. Get all securities \
   `GET /securities`

4. Update security \
   `PUT /securities/{id}`

```json
{
  // new body
  "secid": "AEDRUB_TOD",
  "regnumber": "",
  "name": "AED/RUB_TOD - AED/РУБ", // nullable
  "emitentTitle": ""
}
```

5. Delete security by id \
   `DELETE /securities/{id}`

### *History*

1. Add history \
   `POST /history`

```json
{
  "secid": "AEDRUB_TOD",
  "tradedate": "2022-03-02",
  "numtrades": 224,
  "open": 3421.15, // nullable
  "close": 2900.8 // nullable
}
```

2. Get hisotry by secid \
   `GET /history/{secid}`

3. Get hisotry by date \
   `GET /history/date/{date}`

4. Update hisotry \
   `PUT /history`

```json
{
  "secid": "AEDRUB_TOD",
  "tradedate": "2022-03-05",
  "numtrades": 224,
  "open": 3421.15, // nullable
  "close": 2900.8 // nullable
}
```

5. Delete hisotry by secid \
   `DELETE /history/{secid}`

### *Info*

1. Get info by secid \
   `GET /info/{secid}`

2. Get all info \
   `GET /info`


### Stack: Scala, Http4s, Cats, cats-effect, doobie, PostgreSQL, Flyway
