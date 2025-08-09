# 1. キーペアの作成
- ECC（楕円曲線暗号）のキーペアを作成
- 楕円曲線（NIST P-256）を指定

```sh
openssl ecparam -name prime256v1 -genkey -noout -out private.key
```

# 2. CSRの作成
- 秘密鍵を使ってCSRを作成

```sh
openssl req -new -key private.key -out csr.pem

# 一例として、プロンプトで以下のサブジェクト情報を登録

You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.

-----
Country Name (2 letter code) [AU]:JP
State or Province Name (full name) [Some-State]:Tokyo
Locality Name (eg, city) []:Minato City
Organization Name (eg, company) [Internet Widgits Pty Ltd]:Example Corp
Organizational Unit Name (eg, section) []:
Common Name (e.g. server FQDN or YOUR name) []:www.example.co.jp
Email Address []:

Please enter the following 'extra' attributes
to be sent with your certificate request
A challenge password []:
An optional company name []:
```

# 3. 自己署名証明書の作成
- 本来は認証局にCSRをもとに証明書を作成してもらうが、テスト用に自分の秘密鍵を使って公開鍵の自己署名証明書を作成

```sh
openssl x509 -req -days 365 -in csr.pem -signkey private.key -out self-certificate.pem -sha256

Certificate request self-signature ok
subject=C=JP, ST=Tokyo, L=Minato City, O=Example Corp, CN=www.example.co.jp
```

# 4. PKCS#12キーストアの作成
- 秘密鍵と作成した証明書を格納したPKCS#12キーストアを作成

```sh
openssl pkcs12 -export -out keystore.p12 -inkey private.key -in self-certificate.pem -passout pass:password123
```

# 5. CSRの中身の確認
```sh
openssl req -in csr.pem -noout -text
```

# 6. 証明書の中身の確認
```sh
openssl x509 -in self-certificate.pem -text -noout
```