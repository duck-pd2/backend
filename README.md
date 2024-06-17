# NCKUPP-Personal backend

## Run Application in CLI

### Windows
```bat
gradlew.bat
gradlew run
```

### Linux/MacOS
```
./gradlew
./gradlew run
```

## API interpretation

`API endpoint`: `https://pd.potatoez.xyz/api/v0`

### User Data
```
 {
    "username":<username: String>,
    "pwd": <pwd: String>
 }
```
<details>
    <summary> <code>POST</code> <code>/register</code><code>register an account</code></summary>
    


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

### Validate User

endpoints below need authorization.

##### Header
```json
{
  "Authorization": "Bearer <token: String>"
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

### Event
```json
{
  "id": "<id: String>",
  "title": "<title: String>",
  "start": "<start: String>", 
  "end": "<end: String>",     
  "description": "<description: String>",
  "eventClass": "<eventClass: String>",
  "color": "<color: String>",
  "tags": "<tags: List<String>>"
}
```

1. time in format `${year}-${month}-${day}T${hour}:${minute}:${second}` or `${year}-${month}-${day}`

    example: `2024-06-16T15:59:59`, `2024-06-16`

2. eventClass is class code like `1122_F723300`

3. color format in hexadecimal

    example: `#F7F7F7`

<details>
    <summary> <code>POST</code> <code>/events</code><code>register event from moodle to db</code></summary>



##### Data

```json
{
  "ics_url": "<url: String>"
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

##### Parameters
```
"?id=<id>"
```

if there's no given id

Success: -status code `200` with JSON object
```json
{
  "data": "List<Event>"
}
```

else

Success: -status code `200` with JSON object
```json
{
  "data": "Event"
}
```

</details>

<details>
<summary><code>POST</code><code>/events/new</code><code>adding new event to server</code></summary>

##### Data
```json
{
  "title": "<title: String>",
  "start": "<start: String>",
  "end": "<end: String>",
  "description": "<description: String>",
  "eventClass": "<eventClass: String>",
  "color": "<color: String>",
  "tags": "<tags: List<String>>"
}
```
Success: -status code `201` with JSON object
```json
{
  "data": "Event"
}
```

Missing parameters: -status code `400` with JSON Object
```json
{
  "message" : "Missing required parameters"
}
```
</details>

<details>
<summary><code>PUT</code><code>/events</code><code>update the event base on given id</code></summary>

##### Parameters
```
"?id=<id>"
```
##### Data

```json
{
  "id": "<id: String>",
  "title": "<title: String>",
  "start": "<start: String>", 
  "end": "<end: String>",     
  "description": "<description: String>",
  "eventClass": "<eventClass: String>",
  "color": "<color: String>",
  "tags": "<tags: List<String>>"
}
```


Success: -status code `201` with JSON object
```json
{
  "data": "Event"
}
```
Missing id: status code `400` with JSON Object
```json
{
  "message" : "Missing ID to update"
}
```

Missing Data: status code `400` with JSON Object
```json
{
  "message" : "Missing required parameters"
}
```

Event not found: status code `404` with JSON Object
```json
{
  "message" : "Event not found"
}
```

Event not modified: status code `304` with JSON Object
```json
{
  "message" : "Event not updated"
}
```

</details>

<details>
<summary><code>DELETE</code><code>/events</code><code>Delete the event base on given id</code></summary>

##### Parameters
```
"?id=<id>"
```
Success: -status code `204` with JSON object
```json
{
  "message": "Event delete success"
}
```
Missing id: status code `400` with JSON Object
```json
{
  "message" : "Missing ID to update"
}
```


</details>

## Q&A

Q: Permission denied when executing ./gradlew in linux

A: `chmod +x gradlew`

Making Issue for problem shooting or bug report or feature requests is welcome.