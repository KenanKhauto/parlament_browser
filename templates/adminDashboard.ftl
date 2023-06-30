<!DOCTYPE html>

<#-- Author: Kenan Khauto -->
<#-- Edited by: Simon Schuett -->

<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard Page</title>
    <style>
        <#include "public/css/style.css">
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            /*border: 2px solid dodgerblue;*/
            padding: 15px;
            text-align: left;
        }

        .cc {
            display: flex;
            justify-content: center;
            align-items: stretch;
            min-height: 50vh;
            flex-wrap: wrap;
        }
    </style>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css"/>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
    <#include "topnav.ftl">
    <#include "sidenav.ftl">
</head>
<body>
</body>
<main>
    <section>

        <div class="container">
            <h1>${title}</h1>
        </div>
        <div class="cc" style="background-color: #282828">
            <div class="card no-hover" style="width: auto" id="Edit-User-Card">
                <div class="card-title">
                    <label><i class="fa-solid fa-users" style="margin-right: 15px"></i>
                        Delete or Promote/Demote
                    </label>
                </div>
                <div class="card-body" style="max-height: 550px">
                    <table style="border-inline-color: #1564b3" class="item-list">
                        <thead>
                        <tr style="border-bottom: 2px solid whitesmoke">
                            <th>Username</th>
                            <th style="border-left: 1px solid whitesmoke; border-right: 1px solid whitesmoke">Role</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <#list users as user>
                            <tr>
                                <td>${user.username}</td>
                                <td style="border-left: 1px solid whitesmoke; border-right: 1px solid whitesmoke">${user.role}</td>
                                <td>
                                    <button onclick="deleteUser('${user.username}')" class="delete-button"
                                            style="margin-left: 10px">
                                        <i class="fa-solid fa-user-slash" style="margin-right: 10px"></i>Delete
                                    </button>
                                    <#if user.role == "user">
                                        <button onclick="promoteToAdmin('${user.username}')">
                                            <i class="fa-solid fa-circle-up" style="margin-right: 10px"></i>Promote to
                                            Admin
                                        </button>
                                    <#else>
                                        <button onclick="demoteToRegular('${user.username}')">
                                            <i class="fa-solid fa-circle-down" style="margin-right: 10px"></i>Demote to
                                            Regular User
                                        </button>
                                    </#if>
                                </td>
                            </tr>
                        </#list>
                        </tbody>

                    </table>
                    <script>
                        function deleteUser(username) {

                            fetch("/admin-dashboard/delete-user", {
                                method: "POST",
                                body: username,
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

                            location.reload();
                        }

                        function promoteToAdmin(username) {


                            fetch("/admin-dashboard/promote-user", {
                                method: "POST",
                                body: username,
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

                            location.reload();

                        }

                        function demoteToRegular(username) {

                            fetch("/admin-dashboard/demote-user", {
                                method: "POST",
                                body: username,
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

                            location.reload();
                        }

                    </script>
                </div>
            </div>

            <div class="card no-hover" style="width: auto" id="Login-Card">
                <div class="card-title">
                    <label><i class="fa-solid fa-user-plus" style="margin-right: 15px"></i>
                        Add New User
                    </label>
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
                        <label for="userType">User Type</label><br>
                        <label style="margin-right: 15px"><input type="radio" id="userRadio" name="userType"
                                                                 value="user"
                                                                 style="vertical-align: middle; margin: 0 8px 0 0">User</label>
                        <label><input type="radio" id="adminRadio" name="userType" value="admin"
                                      style="vertical-align: middle; margin: 0 8px 0 0">Admin</label>
                        <br><br>
                        <input class="login-btn" type="button" value="Add User" onclick="addUser()">
                        <p id="message"></p>
                    </form>

                    <script>
                        function addUser() {
                            let username = document.getElementById("username").value;
                            let password = document.getElementById("password").value;
                            let email = document.getElementById("email").value;
                            let birthday = document.getElementById("birthday").value;
                            let role;

                            if (document.getElementById("userRadio").checked) {
                                role = "user";
                            } else if (document.getElementById("adminRadio").checked) {
                                role = "admin";
                            }

                            const newAccount = {
                                username: username,
                                email: email,
                                password: password,
                                birthday: birthday,
                                role: role
                            }

                            fetch("/admin-dashboard/add-user", {
                                method: "POST",
                                body: JSON.stringify(newAccount),
                                headers: {
                                    "Content-Type": "application/json"
                                }
                            })
                                .then(response => response.json())
                                .then(data => {
                                    console.log("Success:", data);
                                    alert("Account created successfully!");
                                })
                                .catch(error => {
                                    console.error("Error:", error);
                                });

                            location.reload();
                        }
                    </script>
                </div>
            </div>
        </div>

        <div class="container panel">
            <h3>Manage Parliament Browser Data</h3>
            <div style="padding-top: 10px; padding-left: 20px">
                <button class="login-btn" type="button" onclick="location.href='/load-parliament'">Go To Raw XML Data
                    <i class="fa-solid fa-arrow-right" style="margin-left: 10px"></i>
                </button>
            </div>

            <div style="padding-top: 10px; padding-left: 20px">
                <button id="reimportAndNlp" class="login-btn" type="button">Reimport Protocols and NLP Analyze Data
                    <i id="rl-spinner" class="fa-solid fa-rotate" style="margin-left: 10px"></i>
                </button>
            </div>
        </div>

        <script>
            $(document).ready(function () {
                $("#reimportAndNlp").on("click", function () {
                    $("#rl-spinner").addClass("fa-spin");
                    $.ajax({
                        method: "GET",
                        url: "/reimportToMongoAndNlp",
                        contentType: "text/html",
                        success: function (data) {
                            console.log(data);
                            $("#rl-spinner").removeClass("fa-spin");
                        },
                        error: function (error) {
                            $("#rl-spinner").removeClass("fa-spin");
                            $("#reimportAndNlp").append(error.responseText);
                        }
                    });
                })
            });
        </script>

    </section>
</main>
<#include "footer.ftl">
</html>