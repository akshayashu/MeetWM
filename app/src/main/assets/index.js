let localVideo = document.getElementById("local-video")
let remoteVideo = document.getElementById("remote-video")

localVideo.style.opacity = 0
remoteVideo.style.opacity = 0

localVideo.onplaying = () => { localVideo.style.opacity = 1 }
remoteVideo.onplaying = () => { remoteVideo.style.opacity = 1 }

var constraints = {
    video: true,
    audio: true
};

var localStream

var listOfCamera = []

navigator.getUserMedia(constraints, (stream) => {
    localVideo.srcObject = stream
    localStream = stream
})

//peer
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

function switchCamera(i){
    navigator.mediaDevices.enumerateDevices().then(function(devices) {
    listOfCamera = [];
      devices.forEach(function(device) {
        'videoinput' === device.kind && listOfCamera.push(device.deviceId);
      });
      console.log(listOfCamera[i]);
      // On my devices:
      // - cameras[0] - front camera;
      // - cameras[1] - back camera;
      constraints = {video: {deviceId: {exact: listOfCamera[i]}}};
      navigator.getUserMedia(constraints, (stream) => {
        // Do something with stream.
        localVideo.srcObject = stream
        localStream = stream
      });
    });
}

function toggleAudio(b) {
    if(b == "true"){
        localStream.getAudioTracks()[0].enabled = true
    }else{
        localStream.getAudioTracks()[0].enabled = false
    }
}