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
	stompClient.send("/morestats/loaddata", {}, JSON.stringify({'accessToken': document.getElementById('accessToken').value}));
}

function showData(message) {
    $("#data").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#send" ).click(function() { loadData(); });
});