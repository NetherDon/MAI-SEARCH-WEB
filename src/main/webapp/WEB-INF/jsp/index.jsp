<head>
    <meta charset="utf-8">
</head>
<body>
    <input id="request" type="text" placeholder="Запрос">
    <input type="button" value="Найти" onclick="search()">
</body>
<style>
    body > * {
        height: 40px;
        font-size: 25;
    }

    #request {
        width: 500px;
    }
</style>
<script>
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
</script>