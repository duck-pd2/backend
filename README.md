# NCKUPP-Personal backend

## API interpretation

`API endpoint`: `http://localhost:8080/api/v0`

<details>
    <summary> <code>POST</code> <code>/register</code><code>register an account</code></summary>
    
##### Data
```
 {
    "username":<username>,
    "pwd": <pwd>
 }
```

##### Response


Success: -status code `200` with JSON object

```json
{
  "token": "<JWT token>"
}
```

Missing Username: -status code `400` with JSON object
```json
{
  "message": "Missing username"
}
```

Missing password: -status code `400` with JSON object
```json
{
  "message": "Missing password"
}
```

Account Exist: -status code `400` with JSON object
```json
{
  "message": "AccountAlreadyExist"
}
```



##### JWT token

```
"exp": 3600s
"user_id": <id>
```

</details>

<details>
    <summary> <code>POST</code> <code>/login</code><code>Login to account</code></summary>

##### Data
```
 {
    "username":<username>,
    "pwd": <pwd>
 }
```

Success: -status code `200` with JSON object

```json
{
  "token": "<JWT token>"
}
```

Missing Username: -status code `400` with JSON object
```json
{
  "message": "Missing username"
}
```

Missing password: -status code `400` with JSON object
```json
{
  "message": "Missing password"
}
```

User Not Found: -status code `401` with JSON object
```json
{
  "message": "UserNotFound"
}
```

Wrong password: -status code `401` with JSON object
```json
{
  "message": "WrongPassword"
}
```

##### JWT token

```

"exp": 3600s
"user_id": <id>

```
</details>


    