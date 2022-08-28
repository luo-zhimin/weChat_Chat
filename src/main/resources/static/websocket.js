function jsonToStr(event){
    let s
    if (typeof event == "string"){
        s=JSON.parse(event);
    }else if (typeof event == "object") {
        s = JSON.stringify(event);
    }
    return s;
}

websocket = null;
//判断当前浏览器是否支持WebSocket，注册socket
if ('WebSocket' in window) {
    //自己的ip
    websocket = new WebSocket('ws://127.0.0.1:8111/webSocket?type=No Authorization');
}
else {
    alert('当前浏览器 不支持实时更新，请切换浏览器')
}

//连接发生错误的回调方法
websocket.onerror = function (e) {
    console.info("WebSocket连接发生错误:"+e);
};

//连接成功建立的回调方法
websocket.onopen = function () {
    console.info("WebSocket连接成功");
}

//连接关闭的回调方法
websocket.onclose = function () {
    console.info("WebSocket连接关闭");
}

//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
window.onbeforeunload = function () {
    closeWebSocket();
}

//将消息显示在网页上
function setMessageInnerHTML(innerHTML) {

    //document.getElementById('message').innerHTML += innerHTML + '<br/>';
}

//关闭WebSocket连接
function closeWebSocket() {
    websocket.close();
}

//发送消息
function socketSend(data) {
    console.log("send-> "+data);
    console.log(JSON.stringify(data))
    this.websocket.send(JSON.stringify(data));
}

//接收到消息的回调方法
/*
介个方法可以放到你需要被改变的位置
*/
websocket.onmessage = function (event) {
    console.log("onmessage-->"+event)
    console.log(typeof event);
    console.log(JSON.stringify(event));
}

