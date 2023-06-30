<!DOCTYPE html>

<#-- Author: Simon Schuett -->

<html lang="en" xmlns="http://www.w3.org/1999/html">
<body>

<nav>

    <div class="flex container" style="justify-content: space-between">

        <div class="flex" style="min-height: 50px">
            <ul class="nav">
                <li>
                    <a href="/deputies">Deputies</a>
                </li>
                <li>
                    <a href="/parties">Parties</a>
                </li>
                <li>
                    <a href="/factions">Factions</a>
                </li>
            </ul>
        </div>

        <div class="flex">
            <form action="/searchGeneral" method="GET" style="position: relative">
                <input type="text" placeholder="Search..." name="term" class="search-field">
                <button type="submit" class="inner-search-btn">
                    <i class="fa-solid fa-magnifying-glass"></i>
                </button>
            </form>

            <#if loggedIn>
                <button class="inverted" onclick="location.href='/logout'" style="margin: 0 10px; padding: 8px 20px">
                    Logout
                </button>
            <#else>
                <button class="inverted" onclick="location.href='/login'" style="margin: 0 10px; padding: 8px 20px">
                    Sign in
                </button>
            </#if>


            <div style="position:relative;">
                <button onclick="toggleList()" class="profile-pic">
                    <i class="fa-solid fa-user" style="width: 20px; height: 20px"></i>
                </button>

                <div id="profile-list"
                     style="display: none; position: absolute; right: 10px; background-color: #222222;
                      border-radius: 15px; padding: 10px; border: 1px solid rgba(240, 240, 240, 0.2); min-width: 150px">
                    <button class="list-button"
                            style="display:none; width: 100%"
                            onclick="location.href='/profile'">Profile
                    </button>
                    <button class="list-button"
                            style="display:none; width: 100%"
                            onclick="location.href='/admin-dashboard'">Admin Dashboard
                    </button>
                </div>
            </div>

            <script>
                function toggleList() {
                    let buttons = document.querySelectorAll(".list-button");
                    for (let i = 0; i < buttons.length; i++) {
                        if (buttons[i].style.display === "none") {
                            buttons[i].style.display = "block";
                        } else {
                            buttons[i].style.display = "none";
                        }
                    }

                    let profileList = document.getElementById("profile-list");
                    profileList.style.display = profileList.style.display === "none" ? "block" : "none";
                }
            </script>

        </div>

    </div>

</nav>

</body>
</html>