var stompClient = null;

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/data', function (data) {
            showData(JSON.parse(data.body).message);
        });
    });
}

function loadData() {
	stompClient.send("/morestats/getdata", {}, JSON.stringify({'jwt': document.getElementById('jwtToken').value}));
}

function showData(message) {
    $("#data").text(message);
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#send" ).click(function() { loadData(); });
});