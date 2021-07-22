let localVideo = document.getElementById("local-video")
let remoteVideo = document.getElementById("remote-video")

localVideo.style.opacity = 1
remoteVideo.style.opacity = 1

localVideo.onplaying = () => { localVideo.style.opacity = 1 }
remoteVideo.onplaying = () => { remoteVideo.style.opacity = 1 }

let localStream

navigator.getUserMedia({
    video: true,
    audio: true
}, (stream) => {
    localVideo.srcObject = stream
    localStream = stream
})

let devices = navigator.mediaDevices.enumerateDevices()
console.log(devices);
// 192.168.0.4
// https://meet-wm.herokuapp.com

//declaring web socket from client-side
// const ws = new io('ws://192.168.0.4:9000/')

// ws.onopen = function () {
//     console.log("WebSocket client is connected.");
// }

// function sendMessage(msg) {
//     ws.send(msg)
// }

// ws.onmessage = function(msg){
//     console.log("Received : " + msg.data)
// }

//per
let peer
function init(userId) {

    peer = new Peer(userId, {
        secure: true,
        host: 'meet-wm.herokuapp.com',
        // '192.168.0.4',
        port: 443,
        path: '/peerjs'
    })


    peer.on('open', () =>{
        // we will write a kotlin function in android over here
    })
    listen()
}

function listen() {

    peer.on('call', (call) => {
            call.answer(localStream)
            call.on('stream', (remoteStream) => {
                remoteVideo.srcObject = remoteStream

                remoteVideo.className = "primary-video"
                localVideo.className = "secondary-video"
            })
        })
        // navigator.getUserMedia({
        //     video: true,
        //     audio: true
        // }, (stream) => {
        //     localVideo.srcObject = stream
        //     localStream = stream

        //     call.answer(stream)
        //     call.on('stream', (remoteStream) => {
        //         remoteVideo.srcObject = remoteStream

        //         remoteVideo.className = "primary-video"
        //         localVideo.className = "secondary-video"
        //     })
        // })
    // })
}

function startCall(otherUserId) {

    navigator.getUserMedia({
        video: true,
        audio: true
    }, (stream) => {

        localVideo.srcObject = stream
        localStream = stream

        const call = peer.call(otherUserId, stream)
        call.on('stream', (remoteStream) => {
            remoteVideo.srcObject = remoteStream

            remoteVideo.className = "primary-video"
            localVideo.className = "secondary-video"
        })
    })

}

function toggleVideo(b) {
    if(b == "true"){
        localStream.getVideoTracks()[0].enabled = true
    }else{
        localStream.getVideoTracks()[0].enabled = false
    }
}


function toggleAudio(b) {
    if(b == "true"){
        localStream.getAudioTracks()[0].enabled = true
    }else{
        localStream.getAudioTracks()[0].enabled = false
    }
}

// navigator.MediaDevices.emulatedDevices(devices -> {console.log(devices.id})