document.addEventListener("DOMContentLoaded", function(){
    console.log("안녕 헤어샵 페이지 나야 js");

    let allShops=[];


    // 유저 위치 정보 받아오는 js
    navigator.geolocation.getCurrentPosition(success, error);

    function success(position) {
        const userLat = position.coords.latitude;
        const userLon = position.coords.longitude;

        console.log("위도: ", userLat, "경도: ", userLon);

        // 유저 위도경도 주소로 변환
        getAddressFromCoords(userLat, userLon);
    }

    function error(err) {
        alert("위치 정보 접근이 거부되었습니다.");
    }

    // 주소 변환 기능

    function getAddressFromCoords(userLat, userLon) {
        fetch(`/api/coord-to-address?x=${userLon}&y=${userLat}`)
            .then(res => res.json())
            .then(data => {
                if(data.userAddress) {
                    console.log("주소: ", data.userAddress);
                    console.log("지역: ", data.region1depth + " " + data.region2depth);

                    const regionText = `${data.region1depth} ${data.region2depth}`;
                    document.getElementById("user-region").textContent = regionText;
                } else {
                    console.log("주소 정보 없음");
                }
            })
            .catch(err => console.error("에러: ", err))
    }

    function error(err) {
        alert("위치 정보를 불러올 수 없습니다.");
        console.error(err);
    }

    // document end
});