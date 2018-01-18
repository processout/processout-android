# ProcessOut Android SDK


## Requirements
Works with android sdk 14+

## Installation

Add Jitpack repository to your build file:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

add this dependency to your build.gradle
```gradle
dependencies {
    compile 'com.github.processout:android-sdk:1.0'
}
```

## Usage

This SDK allows you to tokenize cards information that can then be sent to your backend
to charge customers.

Here is how it works:

``` java
final ProcessOut p = new ProcessOut(this, "your_project_id");
Card c = new Card("Jeremy lejoux","4242424242424242", 11, 19, "123");
p.tokenize(c, new TokenCallback() {
    @Override
    public void onError(POErrors error) {
        Log.e("PO", String.valueOf(error));
    }

    @Override
    public void onSuccess(String token)
    {
        // send the card token to your backend for charging
    }
});

// Update a cvc when needed
p.updateCvc(new Card("card_token", "124"), new CvcUpdateCallback() {
    @Override
    public void onSuccess() {
        // CVC updated
    }

    @Override
    public void onError(POErrors error) {
        // error
    }
});
```

## Error handling
Whenever an error is triggered we return an enum value which can be:

```
NetworkError,
InternalError,
BadRequest,
AuthorizationError,
ParseError,
```

