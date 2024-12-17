document.addEventListener("DOMContentLoaded", function () {
  let target = document.getElementById("target");
  let cropper = new Cropper(target);
  const video = document.querySelector("#video");
  const fileInput = document.getElementById("fileInput");
  const cropButton = document.getElementById("cropButton");
  const preview = document.getElementById("preview");
  const generateButton = document.getElementById("generateButton");
  const ocrForm = document.getElementById("ocrForm");
  const imageDataField = document.getElementById("imageData");

  // カメラ初期化
  navigator.mediaDevices.getUserMedia({ video: true, audio: false })
    .then(stream => {
      video.srcObject = stream;
    })
    .catch(error => console.error("カメラへのアクセスに失敗しました:", error));

  // 撮影ボタン
  document.getElementById("shoot").addEventListener("click", function () {
    const canvas = document.createElement("canvas");
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    const context = canvas.getContext("2d");
    context.drawImage(video, 0, 0, canvas.width, canvas.height);

    // 撮影した画像をtargetにセット
    target.src = canvas.toDataURL("image/png");
    cropper.replace(target.src); // Cropper.jsの画像を更新
  });

  // ファイル選択
  fileInput.addEventListener("change", function (event) {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = function (e) {
        target.src = e.target.result;
        cropper.replace(target.src); // Cropper.jsの画像を更新
      };
      reader.readAsDataURL(file);
    }
  });

  // トリミングボタン
  cropButton.addEventListener("click", function () {
    const canvas = cropper.getCroppedCanvas();
    if (canvas) {
      preview.src = canvas.toDataURL("image/png"); // プレビュー表示
    }
  });

  // 生成ボタン
	generateButton.addEventListener("click", function (event) {
      event.preventDefault(); // フォームのデフォルト送信を防止

      const canvas = cropper.getCroppedCanvas();
      if (canvas) {
        const imageData = canvas.toDataURL("image/png");
        
        // hiddenフィールドに画像データをセット
        imageDataField.value = imageData;

        // フォームを送信
        ocrForm.submit();
      } else {
        console.error("画像データが取得できませんでした");
      }
    });
});
