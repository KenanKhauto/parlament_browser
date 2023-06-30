<!DOCTYPE html>

<#-- Author: Simon Schuett, Stanley Mathew -->

<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${title}</title>

    <META NAME="author" CONTENT="Simon Schuett">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
    <script src="https://d3js.org/d3.v7.min.js"></script>

    <style>
        <#include "public/css/style.css">
    </style>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css"/>

    <script>
        <#include "javascript/d3charts.js">
    </script>

    <script>

        $(document).ready(function () {

            $("#tab-bar").on("click", "button", function () {
                $(".tab-btn").each(function () {
                    $(this).removeClass("active");
                });
                $(this).addClass("active");

                    $(".tab").each(function () {
                        $(this).css("display", "none");
                    });

                let tabID = $(this).attr("id").split("-")[1];
                document.getElementById("tab-" + tabID).style.display = "flex";
            })

            let tabCount = 0;
            $("form").submit(function () {
                document.getElementById("analysis-container").style.display = "block";

                let startDate = document.getElementById("startDate").value;
                let endDate = document.getElementById("endDate").value;
                let groupBy = document.getElementById("groupBy").value;
                let searchFilter = document.getElementById("searchFilter").value;

                if (endDate < startDate) {
                    alert("End Date has to be after the start date!")
                    return false;
                }

                //*********************      NEW  from here      **********************

                const timeFilter = {
                    startDate: startDate,
                    endDate: endDate,
                }

                fetch("graphToken", {
                    method: "POST",
                    body: JSON.stringify(timeFilter),
                    headers: {
                        "Content-Type": "application/json"
                    }
                })
                    /*.then(response => {
                        if (response.redirected) {
                            window.location.href = response.url;
                        }
                        return response.json();
                    })*/
                    .then(data => {
                        console.log("Success:", data);
                        let docs = JSON.parse(data);
                        console.log(docs);

                        chart = LineChart(docs, {
                            x: d => d._id,
                            y: d => d.count,
                            yLabel: "     ↑  count token",
                            width: 750,
                            height: 500,
                            color: "yellow"
                        })

                        console.log(chart)
                        $("#tokenDist").html(chart);
                        $("#rl-token").removeClass("fa-spin");


                    })
                    .catch(error => {
                        console.error("Error:", error);
                    });

//*********************        NEW   upto here      **********************

                document.getElementById("startDate").value = "";
                document.getElementById("endDate").value = "";
                document.getElementById("groupBy").selectedIndex = 0;
                document.getElementById("searchFilter").value = "";

                let buttonTitle = "";
                buttonTitle += searchFilter !== "" ? searchFilter : "No text filter"
                buttonTitle += startDate && endDate ? " <i class='fa-solid fa-calendar-days' style='margin: 0 5px 0 10px'></i>"
                    + startDate + " - " + endDate + " " : " ";
                buttonTitle += groupBy !== "" ? "Grouped: " + groupBy : "";

                $("#tab-bar").append(`
                    <button id="btn-` + tabCount + `" class="tab-btn">`
                    + buttonTitle +
                    `</button>
                    `);

                $("#analysis-panels").append(`
                    <div class="flex container tab" id="tab-` + tabCount + `" style="display: none">

                        <div class="card" style="min-width: 500px">
                            <div class="card-title">
                                <label>Token Quantity</label>
                                <button id="load-tokenQty"><i id="rl-token" class="fa-solid fa-rotate"></i></button>
                            </div>
                            <div class="card-body" style="max-height: 600px">
                                <div id="tokenDist"></div>
                            </div>
                        </div>

                        <div class="card" style="min-width: 500px">
                            <div class="card-title">
                                <label>POS Quantity</label>
                                <button id="load-posQty"><i id="rl-pos" class="fa-solid fa-rotate"></i></button>
                            </div>
                            <div class="card-body" style="max-height: 600px">
                                <div id="posDist"></div>
                            </div>
                        </div>

                        <div class="card" style="min-width: 500px">
                            <div class="card-title">
                                <label>Sentiment Distribution</label>
                                <button id="load-sentiment"><i id="rl-sentiment" class="fa-solid fa-rotate"></i></button>
                            </div>
                            <div class="card-body" style="max-height: 600px">
                                <div id="sentDist"></div>
                            </div>
                        </div>

                        <div class="card" style="min-width: 500px">
                            <div class="card-title">
                                <label>Named Entities Quantity Person</label>
                                <button id="load-entities"><i id="rl-entities" class="fa-solid fa-rotate"></i></button>
                            </div>
                            <div class="card-body" style="max-height: 600px; max-width: 800px">
                                <div id="namedEntDistPER"></div>
                            </div>
                        </div>

                        <div class="card" style="min-width: 500px">
                            <div class="card-title">
                                <label>Named Entities Quantity Location</label>
                                <button id="load-entities"><i id="rl-entities" class="fa-solid fa-rotate"></i></button>
                            </div>
                            <div class="card-body" style="max-height: 600px; max-width: 800px">
                                <div id="namedEntDistLOC"></div>
                            </div>
                        </div>


                        <div class="card" style="min-width: 500px">
                            <div class="card-title">
                                <label>Named Entities Quantity Organisation</label>
                                <button id="load-entities"><i id="rl-entities" class="fa-solid fa-rotate"></i></button>
                            </div>
                            <div class="card-body" style="max-height: 600px; max-width: 800px">
                                <div id="namedEntDistORG"></div>
                            </div>
                        </div>



                        <div class="card" style="min-width: 500px">
                            <div class="card-title">
                                <label>Speaker Speech Count</label>
                                <button id="load-speakerSpeeches"><i id="rl-speakerSpeeches" class="fa-solid fa-rotate"></i></button>
                            </div>
                            <div class="card-body" style="max-height: 600px; max-width: 800px">
                                <div id="speakerDist"></div>
                            </div>
                        </div>

                        <div class="card" style="min-width: 500px">
                            <div class="card-title">
                                <label>Vote Results</label>
                                <button id="load-voteResults"><i id="rl-voteResults" class="fa-solid fa-rotate"></i></button>
                            </div>
                            <div class="card-body" style="max-height: 600px">
                                <div id="vote-results"></div>
                            </div>
                        </div>
                    </div>
                `);


                $("#rl-token").addClass("fa-spin");
                $("#rl-pos").addClass("fa-spin");
                $("#rl-sentiment").addClass("fa-spin");
                $("#rl-entities").addClass("fa-spin");
                $("#rl-speakerSpeeches").addClass("fa-spin");
                $("#rl-voteResults").addClass("fa-spin");

                $.ajax({
                    method: "GET",
                    url: "/tokenDistribution",
                    contentType: "application/json",
                    success: function (data) {
                        let docs = JSON.parse(data);
                        console.log(docs);

                        chart = LineChart(docs, {
                            x: d => d._id,
                            y: d => d.count,
                            yLabel: "     ↑  count token",
                            width: 750,
                            height: 500,
                            color: "yellow"
                        })

                        console.log(chart)
                        $("#tokenDist").html(chart);
                        $("#rl-token").removeClass("fa-spin");
                    },
                    error: function (error) {
                        $("#tokenDist").html(error.responseText);
                    }
                });

                $.ajax({
                    method: "GET",
                    url: "/posDistribution",
                    contentType: "application/json",
                    success: function (data) {
                        let docs = JSON.parse(data);
                        console.log(docs);
                        chart = HorizontalBarChart(docs, {
                            x: d => d.count,
                            y: d => d._id,
                            yDomain: d3.groupSort(docs, ([d]) => -d.count, d => d._id), // sort by descending frequency
                            xLabel: "    count  POS  →",
                            width: 750,
                            height: 500,
                            color: "red"
                        })
                        console.log(chart)
                        $("#posDist").html(chart);
                        $("#rl-pos").removeClass("fa-spin");
                    },
                    error: function (error) {
                        $("#posDist").html(error.responseText);
                    }
                });

                $.ajax({
                    method: "GET",
                    url: "/sentimentDistribution",
                    contentType: "application/json",
                    success: function (data) {
                        let docs = JSON.parse(data);
                        console.log(docs);
                        chart = PieChart(docs, {
                            name: d => d._id,
                            value: d => d.count,
                            width: 500,
                            height: 500,
                            colors: ["forestgreen", "crimson", "steelblue"]
                        });

                        console.log(chart)
                        $("#sentDist").html(chart);
                        $("#rl-sentiment").removeClass("fa-spin");
                    },
                    error: function (error) {
                        $("#sentDist").html(error.responseText);
                    }
                });

                $.ajax({
                    method: "GET",
                    url: "/namedEntityDistributionPER",
                    contentType: "application/json",
                    success: function (data) {
                        let docs = JSON.parse(data);
                        console.log(docs);

                        /* chart = MultiLineChart(docs, {
                             x: d => d._id,
                             y: d => d.count,
                             z: d => d.NETyp,
                             yLabel: "   count",
                             width : 1500,
                             height: 500,
                             color: "green",
                             //voronoi // if true, show Voronoi overlay
                         })*/

                        chart = BarChart(docs, {
                            x: d => d._id,
                            y: d => d.count,
                            xDomain: d3.groupSort(docs, ([d]) => -d.count, d => d._id),
                            yLabel: "    ↑    count named Entity",
                            width: 1000,
                            height: 500,
                            color: "orange"
                        })

                        console.log(chart)
                        $("#namedEntDistPER").html(chart);
                        $("#rl-entities").removeClass("fa-spin");
                    },
                    error: function (error) {
                        $("#namedEntDistPER").html(error.responseText);
                    }
                });


                $.ajax({
                    method: "GET",
                    url: "/namedEntityDistributionLOC",
                    contentType: "application/json",
                    success: function (data) {
                        let docs = JSON.parse(data);
                        console.log(docs);

                        chart = LineChart(docs, {
                            x: d => d._id,
                            y: d => d.count,
                            yLabel: "     ↑  count token",
                            width: 750,
                            height: 500,
                            color: "green"
                        })

                        console.log(chart)
                        $("#namedEntDistLOC").html(chart);
                        $("#rl-entities").removeClass("fa-spin");
                    },
                    error: function (error) {
                        $("#namedEntDistLOC").html(error.responseText);
                    }
                });


                $.ajax({
                    method: "GET",
                    url: "/namedEntityDistributionORG",
                    contentType: "application/json",
                    success: function (data) {
                        let docs = JSON.parse(data);
                        console.log(docs);
                        chart = BarChart(docs, {
                            x: d => d._id,
                            y: d => d.count,
                            xDomain: d3.groupSort(docs, ([d]) => -d.count, d => d._id),
                            yLabel: "    ↑    count named Entity",
                            width: 1000,
                            height: 500,
                            color: "darkred"
                        })

                        console.log(chart)
                        $("#namedEntDistORG").html(chart);
                        $("#rl-entities").removeClass("fa-spin");
                    },
                    error: function (error) {
                        $("#namedEntDistORG").html(error.responseText);
                    }
                });


                $.ajax({
                    method: "GET",
                    url: "/speakerDistribution",
                    contentType: "application/json",
                    success: function (data) {
                        let docs = JSON.parse(data);
                        console.log(docs);
                        chart = BarChart(docs, {
                            x: d => d._id,
                            y: d => d.count,
                            xDomain: d3.groupSort(docs, ([d]) => -d.count, d => d._id),
                            yLabel: "    ↑ count speeches of speaker   ",
                            width: 1000,
                            height: 500,
                            color: "steelblue"
                        })
                        console.log(chart)
                        $("#speakerDist").html(chart);
                        $("#rl-speakerSpeeches").removeClass("fa-spin");
                    },
                    error: function (error) {
                        $("#speakerDist").html(error.responseText);
                    }
                });


                $("#btn-" + tabCount).click();
                tabCount++;
                $.post($(this).attr("action"), $(this).serialize(), function (response) {
                }, "json");
                return false;

            })

        })

    </script>

</head>
<body>

<#include "topnav.ftl">
<#include "sidenav.ftl">

<main>
    <div class="container">
        <h1>NLP Analysis of Speeches</h1>
    </div>
    <section>
        <div class="container">
            <form action="#" method="post">
                <table>
                    <thead>
                    <tr>
                        <th>Start Date</th>
                        <th></th>
                        <th>End Date</th>
                        <th></th>
                        <th></th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>
                            <input type="date" name="startDate" id="startDate">
                        </td>
                        <td>
                            <i class="fa-solid fa-minus" style="margin: 0 10px"></i>
                        </td>
                        <td>
                            <input type="date" name="endDate" id="endDate">
                        </td>
                        <td style="position: relative">
                            <select name="groupBy" id="groupBy" style="padding-right: 40px; margin-left: 20px">
                                <option disabled selected value="">--- Group By ---</option>
                                <option>Month</option>
                                <option>Week</option>
                            </select>
                            <i class="fa-solid fa-chevron-down" style="position: absolute; right: 0; margin: 18px">
                            </i>
                        </td>
                        <td>
                            <div style="position: relative">
                                <input type="text" placeholder="Search..." name="search" class="search-field"
                                       id="searchFilter">
                                <button type="submit" class="inner-search-btn">
                                    <i class="fa-solid fa-magnifying-glass"></i>
                                </button>
                            </div>
                        </td>
                        <td>
                            <button id="AddAnalysis">Add</button>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </form>
        </div>
    </section>

    <div class="flex container" id="tab-bar"
         style="overflow-x: scroll; flex-wrap: nowrap; padding-top: 10px; padding-bottom: 10px; background-color: #282828">

    </div>

    <section style="padding: 0; background-color: #282828">

        <div id="analysis-container" style="background-image: url('notbundestagbutowncopyright_blur.jpeg'); background-size: cover;
        box-shadow: 0 60px 32px -20px inset #282828; display: none">

            <div id="analysis-panels" style="padding-top: 80px">

            </div>

        </div>
    </section>


    <#include "footer.ftl">

</main>

</body>
</html>