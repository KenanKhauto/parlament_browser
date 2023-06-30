<!DOCTYPE html>
<#-- Author: Kenan Khauto -->
<#-- Edited by: Simon Schuett -->

<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login Page</title>

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
                        <input type="text" id="username" name="username" size="30" style="margin-bottom: 20px;">
                        <br>
                        <label for="password">Password</label><br>
                        <input type="password" id="password" name="password" size="30">
                        <br><br>
                        <input class="login-btn" type="button" value="Login" onclick="login()">
                        <p id="message"></p>
                        <p>
                            <small>
                                Don't have an account?
                                <a href="/create-account" style="color: #58adff">Create an account</a>.
                            </small>
                        </p>
                    </form>
                </div>
            </div>

        </div>
    </section>

</main>

<script>
    function login() {
        let username = document.getElementById("username").value;
        let password = document.getElementById("password").value;

        const loggedUser = {
            username: username,
            password: password,

        }


        fetch("/login", {
            method: "POST",
            body: JSON.stringify(loggedUser),
            headers: {
                "Content-Type": "application/json"
            }
        })
            .then(response => {
                if (response.redirected) {
                    window.location.href = response.url;
                }
                return response.json();
            })
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