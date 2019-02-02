<p align="center">
  <img src="./w3tec-logo.png" alt="w3tec" width="400" />
</p>

<h1 align="center">Quick Tournament API</h1>

<br />

![divider](./w3tec-divider.png)

## ❯ Why

![divider](./w3tec-divider.png)

## ❯ Table of Contents

- [Getting Started](#-getting-started)
- [Database](#-database)
- [License](#-license)

![divider](./w3tec-divider.png)

## ❯ Getting Started

![divider](./w3tec-divider.png)

## ❯ Database

### Reading UUID bytes
Use hex() to convert the UUID bytes back to their hexadecimal representation.

```sql
SELECT id, name FROM product;
/* BLOB, 'car' */
SELECT hex(id), name FROM product;
/* 'BFF641BA9F3A4584A1BA53824E7AB3B9', 'car' */
```

Let’s query for a certain UUID using unhex().

```sql
SELECT hex(id), name FROM product
   WHERE id = unhex('BFF641BA9F3A4584A1BA53824E7AB3B9');

/* or if you have a UUID with dashes: */
SELECT hex(id), name FROM product
   WHERE id = unhex(replace("2b08e375-275d-473e-910d-32700e34b61a", '-', ''));
```

![divider](./w3tec-divider.png)

## ❯ License

[MIT](/LICENSE)
