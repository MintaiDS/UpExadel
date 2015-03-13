//ALL UI LOGIC
//ALL DAY


// ----------- VARS

var chatView;  
var messageList = [];
var globalMesID = 0;


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
    
    
    
    //NewMessageAdd
    $("#SendBtn").click(userMessageSended);
    $(document).keypress(function(e) {
    if(e.which == 13 && !e.shiftKey) {
        userMessageSended(e);
        }
    });


    
    $('.SettingsButton').on('click', function(){
            $( "#mainRow" ).transition({ y: '-150px' });
//  $( "#mainRow" ).animate({
//      
//    
//    marginTop:50px;
//    
//  }, 300, function() {
//    // Animation complete.
//  });
	   });


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
    //$("#MessageForm").blur();


}; 




var userMessageSended = function(evt){
    var messageForm = $("#MessageForm");
    var message = newMessage(1,globalMesID++,"Anton",messageForm.val());
    
    if (message.mesText.length == 0){
        return;
    }
    messageForm.val("");
    $("#MessageForm").blur();

    //checkServerStatus();
    
    //postMessage(message);
    addNewMessage(message);
    store()
    

    scrollToBottom(chatView,true);


};


var userMessageDelete = function(evt){
    
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
    alert("Some Awesome View goes Here And we Do some cool stuff by editing a message")

};    








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








