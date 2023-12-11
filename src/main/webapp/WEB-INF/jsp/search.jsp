<head>
    <meta charset="utf-8">
</head>
<body>
    <div id="search">
        <input id="request" type="text" placeholder="Запрос" value="${query}">
        <input type="button" value="Найти" onclick="search()">
        <input type="button" value="Главная" onclick="openMainPage()">
    </div>
    <label id="time">Времени затрачено: ${time}</label>
    <div id="page-list" style="margin-top: 30px;">
        ${pages}
    </div>
    <br><input id="more-button" type="button" value="Показать еще" onclick="loadMore()">
    <br><label id="post"></label>
</body>
<style>
    #search > * {
        height: 40px;
        font-size: 25;
    }

    #request {
        width: 500px;
    }

    #page-list {
        width: fit-content;
    }

    #page-list > div {
        border: 1px black solid;
        padding: 5px;
        margin: 5px 0px;
    }

    #page-list > div > a[href] {
        font-weight: bold;
        font-size: 20;
        color: dodgerblue;
        text-decoration: none;
    }

    #page-list > div > a[href]:hover {
        text-decoration: underline;
    }
</style>
<script>
    function openMainPage()
    {
        open("/", "_self");
    }
    
    function search()
    {
        let req = document.getElementById("request");
        if (req.value != "")
        {
            let url = new URLSearchParams();
            url.append("text", req.value);
            open("/search?" + url.toString(), "_self");
        }
    }

    function loadMore()
    {
        console.log("Get more request sent");
        fetch("/search/more", { method: "GET" }).then((response) =>
        {
            console.log("Pages are loaded");
            let post = document.getElementById("post");
            let btn = document.getElementById("more-button");

            if (!response.ok)
            {
                post.innerHTML = "Ошибка запроса";
                return;
            }

            response.text().then((a) => 
            {
                let data = JSON.parse(a);
                let list = document.getElementById("page-list");
            
                list.innerHTML += data.pages;
                post.innerHTML = "Времени затрачено: " + data.time + " c";

                if (data.pages == "")
                {
                    post.innerHTML += "; Результатов не найдено";
                    btn.hidden = true;
                }
            });
        });
    }
</script>