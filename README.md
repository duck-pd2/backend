# NCKUPP-Personal backend

## API interpretation

`API endpoint`: `http://localhost:8080/api/v0`

<details>
    <summary> <code>POST</code> <code>/register</code><code>register an account</code></summary>
    
##### Data
```
 {
    "username":<username: String>,
    "pwd": <pwd: String>
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
    "username":<username: String>,
    "pwd": <pwd: String>
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

<details>
    <summary> <code>POST</code> <code>/events</code><code>register event from moodle to db</code></summary>

##### Header
```json
{
  "Authorization": "Bearer <token: String>"
}
```

##### Data

```json
{
  "ics_url": "<url: String>"
}
```

##### Event
```json
{
  "title": "<title: String>",
  "start": "<start: String>",
  "end": "<end: String>",
  "description": "<description: String>",
  "eventClass": "<eventClass: String>"
}
```

Success: -status code `201` with JSON object
```json
{
  "data": "List<Event>"
}
```

Missing JWT token: -status code `401` with JSON Object
```json
{
  "message" : "Token to access HttpHeaders.WWWAuthenticate Bearer realm=potatoez.xyz is either invalid or expired."
}
```

Missing claim `user_id` in JWT token: -status code `400` with JSON object

```json
{
  "message": "invalid credentials"
}
```

user id invalid: -status code `404` with JSON object

```json
{
  "message": "user not found"
}
```

Missing url: -status code `400` with JSON object
```json
{
  "message": "Missing ICS URL"
}
```

</details>

<details>
    <summary> <code>GET</code> <code>/events</code><code>get user events</code></summary>

##### Header
```json
{
  "Authorization": "Bearer <token: String>"
}
```

##### Event
```json
{
  "title": "<title: String>",
  "start": "<start: String>",
  "end": "<end: String>",
  "description": "<description: String>",
  "eventClass": "<eventClass: String>"
}
```

Success: -status code `200` with JSON object
```json
{
  "data": "List<Event>"
}
```

Missing JWT token: -status code `401` with JSON Object
```json
{
  "message" : "Token to access HttpHeaders.WWWAuthenticate Bearer realm=potatoez.xyz is either invalid or expired."
}
```

Missing claim `user_id` in JWT token: -status code `400` with JSON object

```json
{
  "message": "invalid credentials"
}
```

user id invalid: -status code `404` with JSON object

```json
{
  "message": "user not found"
}
```


</details>
    