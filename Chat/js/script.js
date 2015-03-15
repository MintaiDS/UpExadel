//ALL UI LOGIC
//ALL DAY


// ----------- VARS

var chatView;  
var messageList = [];
var globalMesID = 0;

var editMode = false;
var settingsMode = false;


var newMessage = function (userID,mesID,name,mesText){
    return {
        userID: userID,
        mesID: mesID,
        name: name,
        mesText: mesText
        
    };
};

// ----------- Main Function

$( document ).ready(function(){

    handleChatSize();
    $( window ).resize(handleChatSize);
    
    chatView = $('.panel-body');
    run();
    
    
    scrollToBottom(chatView,false);
    chatView.css("opacity","100");
    
    $("#MessageForm").focus();
    
    
    //NewMessageAdd
    $("#SendBtn").click(userMessageSended);
    registerKeyPress(editMode);

//Additional setup
    $("#EditBtn").click(editMessageEditMode);
    $("#CancelEditBtn").click(cancelEditMode);
    
    
    $('.SettingsButton').on('click', toggleSettings);
        

	   


});

// ----------- LOGIC. Chat UI updates. Server Requests

function run() { // start of app. data restore.
    
    var data = restore() ;
    createAllMessages(data);
    
}


function createAllMessages(data) {
    if (data != null) {
	for(var i = 0; i < data.length; i++)
		addMessageFromData(data[i]);
    };    
}

function restore() {
	if(typeof(Storage) == "undefined") {
		alert('localStorage is not accessible');
		return;
	}
    
	var item = localStorage.getItem("data");
    var mesIDopt = localStorage.getItem("mesID");
    if (mesIDopt != null){
        globalMesID = mesIDopt;
    }

	return item && JSON.parse(item);
}

function store(){
    if(typeof(Storage) == "undefined") {
		alert('localStorage is not accessible');
		return;
	}

	localStorage.setItem("data", JSON.stringify(messageList));
    localStorage.setItem("mesID", globalMesID);
}

function addMessageFromData(message){
    addNewMessage(message);
    
}

function deleteFromList(mesID){
    for (var i = 0 ; i < messageList.length ; i++){
        if (messageList[i].mesID == mesID){
            messageList.splice(i,1);
            store();
            continue;
        }
    }

}


// Test Func. It woorks.
function postMessage(message){ 
    $.ajax({
    type: "POST",
    url: "http://10.160.63.123:997/chat",
    data: JSON.stringify({ name: "John", message: message })
    }).done(function(ms){
        alert("done");
    });
}


// UI
function addNewMessage(message){

    var messBlock = $(".media-list > .media:first").clone();
    messBlock.find(".message").text(message.mesText);
    messBlock.attr("data-mesID",[message.mesID]);
     
    messBlock.removeClass("hidden"); 
    messBlock.find(".deleteMessage").click(userMessageDelete); 
    messBlock.find(".editMessage").click(userMessageEdit);
    messBlock.appendTo(".media-list");

    messageList.push(message);
    


}; 




var userMessageSended = function(evt){
    
    var messageForm = $("#MessageForm");
    var message = newMessage(1,globalMesID++,"Anton",messageForm.val());
    
    if (message.mesText.length == 0){
        return;
    }
    messageForm.val("");
    

    //checkServerStatus();
    
    //postMessage(message);
    addNewMessage(message);
    store()
    
    
    scrollToBottom(chatView,true);
    
    messageFormRefresh();
    
    
    
    
   
    

};


var userMessageDelete = function(evt){
    
    if (editMode)
        return;
    
    var sure = confirm("Delete Message?"); // TODO: - Nice alert view. And Animation
    if (!sure) {
        return false;
    }
    var messageToDelete = $(this).parents("li.media");
    var mesIDtoDelete = parseInt(messageToDelete.attr("data-mesID"));
    alert(mesIDtoDelete);
    deleteFromList(mesIDtoDelete);
    messageToDelete.remove(); //TODO : - Awesome animation ASAP
};


var userMessageEdit = function(evt){
    // - TODO - EDIT
    
    if (editMode)
        return;
    
    var messageToEdit = $(this).parents("li.media");
    messageToEditGlobal = messageToEdit.find(".message");
    
    UIToggleEditMode();
    

}; 

var messageToEditGlobal;

var editMessageEditMode = function(){
    if (!editMode)
        return;
    var text = $("#MessageForm").val();
    if (text.length == 0 )
        return;
    
    
    messageToEditGlobal.text($("#MessageForm").val());
    cancelEditMode();
}

var cancelEditMode = function(){
    if (!editMode)
        return;
    UIToggleEditMode();
}


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







// Does not works. Need to teach server to make callbacks.
var serverURL = "http://192.168.18.38";
function checkServerStatus(){ // Rethink
    $.ajax({url: serverURL,
        type: "HEAD",
        timeout:1000,
        statusCode: {
            200: function (response) {
                alert('Working!');
            },
            400: function (response) {
                alert('Not working!');
            },
            0: function (response) {
                alert('Not working!');
            }              
        }
 });
}



// ----------- UI details you do not need to carry about. 
// Really. Don't

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
           userMessageSended(e); 
        }
        
        }
    });
}


function messageFormRefresh(){
    $("#MessageForm").val("");
    $("#MessageForm").blur();
    setTimeout(function(){
        $("#MessageForm").focus();
    }, 50);
    
    
    
};
  
var settingsHeight = "-120px"

var toggleSettings = function(){
    if (settingsMode){
        
        settingsMode = false;
            $("#SettingsButtonGlyph").removeClass("coloredGlyph");
        $( "#mainRow" ).transition({ y: '0px' });
        $('.settings').transition({ opacity: 0});
        $(".settings").css("box-shadow", "none");
        

    } else {
        
        settingsMode = true;
        $(".settings").css("box-shadow", "0px 0px 3px #888888"); $("#SettingsButtonGlyph").addClass("coloredGlyph");
        $( "#mainRow" ).transition({ y: settingsHeight,
                                   });
        $('.settings').transition({ opacity: 100 });


    }

};  








