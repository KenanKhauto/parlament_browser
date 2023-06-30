<!DOCTYPE html>

<#-- Author: Kenan Khauto -->
<#-- Edited by: Simon Schuett -->

<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Profile</title>
    <style>
        <#include "public/css/style.css">
        .profile-container {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        .edit-btn:hover {
            background-color: #1564b3;
        }
        .stop-edit-btn:hover {
            background-color: #1564b3;
        }
    </style>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css"/>
    <#include "topnav.ftl">
    <#include "sidenav.ftl">
</head>

<body>
</body>
<main>

    <div class="container">
        <h1>${user.getUsername()}</h1>
    </div>

    <section class="container">

        <div class="card no-hover" style="max-width: 600px" id="Login-Card">
            <div class="card-title">
                <label><i class="fa-solid fa-user" style="margin-right: 15px"></i>Your Account</label>
                <div>
                    <button id="cancel-edit-btn" class="stop-edit-btn btn-cancel" type="button" value="Stop Editing"
                            onclick="location.href='/profile'" style="display: none; float: none; margin-right: 10px">
                        Cancel
                    </button>
                    <button id="submit-edit-btn" class="btn-done" type="button" value="Submit Changes"
                            onclick="editUser()" style="display: none">
                        Done
                    </button>
                    <button id="edit-btn" class="edit-btn" type="button" value="Edit"
                            style="float: none; border: 1px solid transparent">
                        <i class="fa-regular fa-pen-to-square"></i> Edit
                    </button>
                </div>
            </div>
            <div class="card-body" style="max-height: 600px">
                <form>
                    <label for="username">Username</label><br>
                    <input type="text" id="username" name="username" size="30" style="margin-bottom: 20px"
                           value="${user.getUsername()}" readonly>
                    <br>
                    <label for="email">Email</label><br>
                    <input type="email" id="email" name="email" size="30" style="margin-bottom: 20px"
                           value="${user.getEmail()}" readonly>
                    <br>
                    <label for="password">Password</label><br>
                    <input type="password" id="password" name="password" size="30" style="margin-bottom: 20px"
                           value="${user.getPassword()}" readonly>
                    <br>
                    <label for="birthday">Birthday</label><br>
                    <input type="date" id="birthday" name="birthday" value="${user.getBirthday()}" readonly>
                    <br><br>


                    <#--<input class="login-btn" type="button" value="Submit Changes" onclick="editUser()">-->
                    <p id="message"></p>
                </form>

                <script>
                    document.querySelector(".edit-btn").addEventListener("click", function () {
                        document.querySelector("#username").removeAttribute("readonly");
                        document.querySelector("#email").removeAttribute("readonly");
                        document.querySelector("#password").removeAttribute("readonly");
                        document.querySelector("#birthday").removeAttribute("readonly");

                        document.getElementById("cancel-edit-btn").style.display = "inline";
                        document.getElementById("submit-edit-btn").style.display = "inline";
                        document.getElementById("edit-btn").style.display = "none";
                    });

                    function editUser() {
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

                        fetch("/profile/edit-user", {
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
                        alert("Your account has been edited successfully!");
                        location.reload();
                    }

                </script>
            </div>
        </div>
    </section>

</main>

<#include "footer.ftl">
</html>