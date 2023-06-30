<!DOCTYPE html>

<#-- Author: Kenan Khauto -->
<#-- Edited by: Simon Schuett -->

<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create Account Page</title>
    <style>
        <#include "public/css/style.css">
    </style>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css"/>

    <#include "topnav.ftl">

    <#include "sidenav.ftl">
</head>
<body>
</body>
<main>

    <section>
        <div class="flex container">

            <div class="card no-hover" style="width: auto" id="Login-Card">
                <div class="card-title">
                    <label>${title}</label>
                </div>
                <div class="card-body" style="max-height: 600px">
                    <form>
                        <label for="username">Username</label><br>
                        <input type="text" id="username" name="username" size="30" style="margin-bottom: 20px">
                        <br>
                        <label for="email">Email</label><br>
                        <input type="email" id="email" name="email" size="30" style="margin-bottom: 20px">
                        <br>
                        <label for="password">Password</label><br>
                        <input type="password" id="password" name="password" size="30" style="margin-bottom: 20px">
                        <br>
                        <label for="birthday">Birthday</label><br>
                        <input type="date" id="birthday" name="birthday">
                        <br><br>
                        <input class="login-btn" type="button" value="Create Account" onclick="createAccount()">
                        <p id="message"></p>
                        <p>
                            <small>
                                Already have an account?
                                <a href="/login" style="color: #58adff">Login</a>
                            </small>
                        </p>
                    </form>
                </div>
            </div>

        </div>
    </section>

</main>
<script>
    function createAccount() {


        let username = document.getElementById("username").value;
        let password = document.getElementById("password").value;
        let email = document.getElementById("email").value;
        let birthday = document.getElementById("birthday").value;
        const newAccount = {
            username: username,
            email: email,
            password: password,
            birthday: birthday
        }

        fetch("/create-account", {
            method: "POST",
            body: JSON.stringify(newAccount),
            headers: {
                "Content-Type": "application/json"
            }
        })
            .then(response => response.json())
            .then(data => {
                console.log("Success:", data);
            })
            .catch(error => {
                console.error("Error:", error);
            });
    }
</script>
<#include "footer.ftl">
</html>