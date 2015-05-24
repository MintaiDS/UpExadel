//UI LOGIC


// ----------- VARS

var chatView;  
var messageList = [];
var editMode = false;
var settingsMode = false;
var currentUserName;

var newUser = function () {
    return {
        status: "new"
    };
};

// Struct for adding message to UI
var newMessage = function (date,username,messageId,messageText){
    return {
        date: date,
        username: username,
        messageId: messageId,
        messageText: messageText
    };
};

// Struct for sending message to server
var newMessageSendRequest = function (username, messageText){

    return  JSON.stringify({
            username: username,
            messageText: messageText,
            status: "new"
        });
};
// Struct for deleting message from server
var deleteMessageRequest = function (username, messageId){
    return  JSON.stringify( {
            messageId: String(messageId),
            username: username,
            status: "delete"
        });
};

var editMessageRequest = function (username, messageId, messageText){
    return  JSON.stringify( {
            messageText: messageText,
            messageId: String(messageId),
            username: username,
            status: "edit"
        });
};

// ----------- Main Function

$( document ).ready(function(){
    handleChatSize();
    $( window ).resize(handleChatSize);
    chatView = $('.panel-body');
    scrollToBottom(chatView,false);
    chatView.css("opacity","100");
    $("#MessageForm").focus();
    //NewMessageAdd
    $("#SendBtn").click(userMessageSended);
    registerKeyPress(editMode);

//Additional setup
    $("#EditBtn").click(editMessageEditMode);
    $("#CancelEditBtn").click(cancelEditMode);
    
//SettingsListeners    
    $('.SettingsButton').on('click', toggleSettings);
    $("#NameSettingsApply").on('click', applySettings);
    $("#CancelSettingsBtn").on('click', toggleSettings);

    run();
});

// ----------- LOGIC. Chat UI s. Server Requests

function run() { // start of app. data restore.
    
    restore() ;
    checkForEditing(currentUserName);
    getAction(newUser());
    longPolling();
}

function longPolling() {
    var xhr = new XMLHttpRequest();
    xhr.open("get","/chat",true);
    xhr.timeout = 300000;
    xhr.send();
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            if (xhr.status == "200") {
                console.log('get message success');
                getActionFromServerWithJSON(xhr.responseText);
            }
            else {
                console.log('get message error');
            }
            longPolling();
        }
    };
}
function createAllMessages(data) {
    if (data != null) {
	for(var i = 0; i < data.length; i++)
		addMessageFromData(data[i]);
    }
}

function restore() {
	if(typeof(Storage) == "undefined") {
		console.log('localStorage is not accessible');
		return;
	}

    var userNameopt = localStorage.getItem("username");
    if (userNameopt != null){
        currentUserName = userNameopt;
        $("#NameForm").val(currentUserName);
    } else {
        currentUserName = "GuestUser";
    }
}

function store(){
    if(typeof(Storage) == "undefined") {
		console.log('localStorage is not accessible');
		return;
	}

    localStorage.setItem("username", currentUserName);
}

function addMessageFromData(message){
    addNewMessage(message);
}

function deleteFromList(messageId){
    for (var i = 0 ; i < messageList.length ; i++){
        if (messageList[i].messageId == messageId){
            messageList.splice(i,1);
        }
    }
}

function editInList(messageId,newText){
    for (var i = 0 ; i < messageList.length ; i++){
        if (messageList[i].messageId == messageId){
            messageList[i].messageText = newText;
        }
    }
}

// Test Func. It works.
function postNewMessage(data){
    var xhr = new XMLHttpRequest();
    xhr.open("post","/chat",true);
    xhr.setRequestHeader('Content-Type', 'text');
    xhr.send(data);
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            if (xhr.status == "200") {
                console.log('get message success');
            }
            else {
                console.log('get message error');
            }
        }
    };
}

function postDeleteMessage(data){
    var xhr = new XMLHttpRequest();
    xhr.open("post","/chat",true);
    xhr.setRequestHeader('Content-Type', 'text');
    xhr.send(data);
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            if (xhr.status == "200") {
                console.log('get message success');
            }
            else {
                console.log('get message error');
            }
        }
    };
}


function postEditMessage(data){
    var xhr = new XMLHttpRequest();
    xhr.open("post","/chat",true);
    xhr.setRequestHeader('Content-Type', 'text');
    xhr.send(data);
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            if (xhr.status == "200") {
                console.log('get message success');
            }
            else {
                console.log('get message error');
            }
        }
    };
}

function getAction(getRequest){
    var xhr = new XMLHttpRequest();
    xhr.open("get","/chat"+"?status=new",true);
    xhr.send(getRequest);
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            if (xhr.status == "200") {
                console.log('get message success');
                getActionFromServerWithJSON(xhr.responseText,"new");
            }
            else {
                console.log('get message error');
            }
        }
    }
}

function getActionFromServerWithJSON(jsonData){
        if (!jsonData)
            return;
        var dataFromServer =$.parseJSON(jsonData);

        var newMessages = dataFromServer.messages;
        for( i = 0; i < newMessages.length; i++){
            var messageFromServer = newMessages[i];
            var status = messageFromServer.status;
            if(status == "new"){ // NEW
                var message = newMessage(messageFromServer.date,
                                        messageFromServer.username,
                                        messageFromServer.messageId,
                                        messageFromServer.messageText);
                addNewMessage(message);
                } else if (status == "delete") { // DELETE
                    var messageIDToDelete = messageFromServer.messageId;
                    deleteMessageByID(messageIDToDelete);
                } else if(status == "edit"){// EDIT
                    var messageIDToEdit = messageFromServer.messageId;
                    var editMessageText = messageFromServer.messageText;
                    editMessageByIDandNewText(messageIDToEdit,editMessageText);
                }
        }
    }

// UI
function addNewMessage(message){
    var messBlock = $(".media-list > .media:first").clone();
    messBlock.find(".message").text(message.messageText);
    messBlock.find(".NickName").text(message.username);
    messBlock.find(".Time").text(message.date);
    messBlock.attr("data-messageID",[message.messageId]);
    messBlock.removeClass("hidden");
    if(currentUserName == message.username){ // SameUser
    messBlock.find(".deleteMessage").click(userMessageDelete); 
    messBlock.find(".editMessage").click(userMessageEdit);
    } else { // Can't delete and edit this message
    messBlock.find(".deleteMessage").remove(); 
    messBlock.find(".editMessage").remove();
    }
    messBlock.appendTo(".media-list");
    messageList.push(message);
	scrollToBottom(chatView,false);
}
function checkForEditing (username){
    var messBlock = $(".media-list");
    for (i = 0; i < messBlock.length; i++){
        var message = messBlock[i];
        if (username == message.username){
            messBlock.find(".deleteMessage").click(userMessageDelete);
            messBlock.find(".editMessage").click(userMessageEdit);
        }
    }
}
var userMessageSended = function(){ // new message sended by user
    var messageForm = $("#MessageForm");
if (nameCheck(currentUserName)) {
    if (isMessageValid(messageForm.val())) {
        var prepareNewMessageForSend = newMessageSendRequest(
            currentUserName,
            messageForm.val());
        messageForm.val("");
        postNewMessage(prepareNewMessageForSend);
        scrollToBottom(chatView,true);
        messageFormRefresh();
    }
}  else {
         alert ("Choose a name first!");
    }
};

function deleteMessageByID(messageId){
    var messBlock = $(".media-list > .media[data-messageID="+messageId+"]");
    messBlock.remove();
    deleteFromList(messageId);
}

function editMessageByIDandNewText(messageId, text){
    var messBlock = $(".media-list > .media[data-messageID="+messageId+"]");
    var textUI = messBlock.find(".message");
    textUI.text(text);
    editInList(messageId,text);
}

var userMessageDelete = function(){
    if (editMode)
        return;
    var sure = confirm("Delete Message?"); // TODO: - Nice alert view. And Animation
    if (!sure) {
        return false;
    }
    var messageToDelete = $(this).parents("li.media");
    var messageIDtoDelete = parseInt(messageToDelete.attr("data-messageID"));
    console.log('delete ' + messageIDtoDelete);
    if (nameCheck(currentUserName)) {
        var messageToDeleteRequestData = deleteMessageRequest(currentUserName, messageIDtoDelete);
        postDeleteMessage(messageToDeleteRequestData);
    } else {
        alert ("Choose a name first!");
    }
};

var userMessageEdit = function(){ // ENTER EDIT MODE
    if (editMode)
        return;
    var messageToEdit = $(this).parents("li.media");
    messageToEditGlobal = messageToEdit.find(".message");
    UIToggleEditMode();
}; 

var messageToEditGlobal;
var editMessageEditMode = function(){ // EDIT MESSAGE 
    if (!editMode)
        return;
    var text = $("#MessageForm").val();
    if (!isMessageValid(text))
        return;
    messageToEditGlobal.text("Editing...");
    var messageIDtoEdit = parseInt(messageToEditGlobal.parents("li.media").attr("data-messageID"));
    if (nameCheck(currentUserName)) {
        var editRequest = editMessageRequest(currentUserName, messageIDtoEdit, text);
        postEditMessage(editRequest);
    } else {
        alert ("Choose a name first!");
    }
    cancelEditMode();
};

var cancelEditMode = function(){
    if (!editMode)
        return;
    UIToggleEditMode();
};

function UIToggleEditMode(){
    if (editMode){
        editMode = false;
        messageFormRefresh();
        messageToEditGlobal.removeClass("editModeEnabled");
        $("#SendBtn").show();
        $("#EditBtn").addClass("hidden");
        $("#CancelEditBtn").addClass("hidden");
        registerKeyPress();
    } else {
        editMode = true;
        registerKeyPress(editMode);
        messageToEditGlobal.addClass("editModeEnabled");
        var textToEdit = messageToEditGlobal.text();
        $("#MessageForm").val(textToEdit);
        $("#MessageForm").focus();
        $("#SendBtn").hide();
        $("#EditBtn").removeClass("hidden");
        $("#CancelEditBtn").removeClass("hidden");
    }
}

// ----------- UI details
 var handleChatSize = function handleChatSize(){
    $('.panel-body').height($('body').height() - $('.panel-footer').height() - 40);
 };

function scrollToBottom(view,animate){
    if(animate){
        view.animate({"scrollTop": $('.panel-body')[0].scrollHeight}, "slow");
    }
    else {
        view.animate({"scrollTop": $('.panel-body')[0].scrollHeight}, 0);
    }     
}

var registerKeyPress = function(editMode){
    $(document).off("keypress");
    $(document).keypress(function(e) {
    if(e.which == 13 && !e.shiftKey) {
        if (editMode){
           editMessageEditMode();    
        } else {
           userMessageSended(); 
        }
    }
    });
};

var registerForNameCheck = function(){
    if(settingsMode){
    $(document).keyup(function(){
    var Newname = $("#NameForm").val();
    if(isMessageValid(Newname)){
        $("#NameSettingsApply").removeClass("disabled");
    } else {
        $("#NameSettingsApply").addClass("disabled");
    }
    })
    } else {
       $(document).off("keyup");
    }
};

function messageFormRefresh(){
    $("#MessageForm").val("");
    $("#MessageForm").blur();
    setTimeout(function(){
        $("#MessageForm").focus();
    }, 50);
}
var settingsHeight = "-120px";
var toggleSettings = function(){
    if (settingsMode){
        settingsMode = false;
         $(".settings").css("z-index", "-1");   $("#SettingsButtonGlyph").removeClass("coloredGlyph");
        $( "#mainRow" ).transition({ y: '0px' });
        $('.settings').transition({ opacity: 0});
        $(".settings").css("box-shadow", "none");
        $(".settings").css("z-index", "-1");
    } else {
        settingsMode = true;
        $(".settings").css("bottom","120px");
        $(".settings").css("box-shadow", "0px 2px 3px #888888"); $("#SettingsButtonGlyph").addClass("coloredGlyph");
        $( "#mainRow" ).transition({ y: settingsHeight
        });
        $('.settings').transition({ opacity: 100 }, function(){
            $(".settings").css("z-index", "0");
        });
    }
    registerForNameCheck();
}; 
var nameCheck = function(curUsername) {
    if (curUsername && curUsername != "") {
        return true;
    } else {
        return false;
    }
};
var applySettings = function(){
    var newName = $("#NameForm").val();
    currentUserName = newName;
    store();
    toggleSettings();
};
    var isMessageValid = function(messageText){
        if (messageText == "")
            return false;
        if (messageText.length == 0 )
            return false;
        for (var i = 0, ch; i < messageText.length; i++ ){
            ch = messageText.charCodeAt(i);
            if(ch != 32 && ch != 10){
            return true;
            }
        }
        return false;
    };