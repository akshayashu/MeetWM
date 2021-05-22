let localVideo = document.getElementById("local-video")
let remoteVideo = document.getElementById("remote-video")

localVideo.style.opacity = 0
remoteVideo.style.opacity = 0

localVideo.onplaying = () => { localVideo.style.opacity = 1 } 
remoteVideo.onplaying = () => { remoteVideo.style.opacity = 1 }

const constraints = {
  video: true,
  audio: true
}

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
        Android.onPeerConnected()
    })
    listen()
}

let localStream
function listen() {

    peer.on('call', (call) => {
        navigator.getUserMedia({
            video: true,
//                {
//                   facingMode: "environment"
//                 },
            audio: true
        }, (stream) => {
            localVideo.srcObject = stream
            localStream = stream

            call.answer(stream)
            call.on('stream', (remoteStream) => {
                remoteVideo.srcObject = remoteStream
                
                remoteVideo.className = "primary-video"
                localVideo.className = "secondary-video"
            })
        })
    })
}

function startCall(otherUserId) {

    navigator.getUserMedia({
        video: true,
//                {
//                   facingMode: "environment"
//                 },
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