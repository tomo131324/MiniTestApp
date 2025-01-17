// ロゴをクリックしたときにサイドバーを開閉するJavaScript
document.getElementById('logo').addEventListener('click', function () {
    var sidebar = document.getElementById('sidebar');
    sidebar.classList.toggle('open');
});

// upload-areaクリックでファイル選択を起動
document.getElementById('upload-area').addEventListener('click', function () {
    document.getElementById('image-upload').click();
});

// 画像プレビューを表示する処理
function displayImagePreview(file) {
    const reader = new FileReader();
    reader.onload = function (e) {
        const uploadArea = document.getElementById('upload-area');
        const existingImage = uploadArea.querySelector('img');
        if (existingImage) existingImage.remove();

        const img = document.createElement('img');
        img.src = e.target.result;
        img.alt = "プレビュー画像";
        img.style.width = '100%';
        img.style.height = '100%';
        img.style.objectFit = 'contain';
        uploadArea.appendChild(img);
    };
    reader.readAsDataURL(file);
}

// ファイル選択時の処理
document.getElementById('image-upload').addEventListener('change', function (event) {
    const file = event.target.files[0];
    if (!file) return;

    // プレビュー表示
    displayImagePreview(file);

    // #file-infoを非表示にする
    document.getElementById('file-info').classList.add('hidden');
    document.getElementById('upload-button').disabled = false;

    // 画像をアップロードする
    uploadImage(file);
});



// ファイル選択時の処理
function handleFileSelect(event) {
    const file = event.target.files[0];
    if (file) {
        // 画像が読み込まれた後の処理
        uploadImage(file);  // 画像をそのまま渡す
    }
}

function handleFileSelect(event) {
    const file = event.target.files[0];
    const fileInput = event.target;
    const fileInfo = document.getElementById("file-info");
    const uploadArea = document.getElementById("upload-area");
    const uploadButton = document.getElementById("upload-button");

    if (fileInput.files.length > 0) {
        // ファイルが選択された場合
        const fileName = fileInput.files[0].name;
        document.getElementById("file-name").textContent = fileName;
        fileInfo.classList.remove("hidden");

        // "クリックしてアップロード" と SVG を非表示
        uploadArea.querySelector("svg").style.display = "none";
        uploadArea.querySelector("p").style.display = "none";

        // "テキスト抽出" ボタンを有効にする
        uploadButton.disabled = false;
    }
}


function uploadImage(event) {
    event.preventDefault();  // フォーム送信を防ぐ

    const fileInput = document.getElementById("image-upload");
    const file = fileInput.files[0];
    const reader = new FileReader();

    reader.onloadend = function () {
        // 画像をBase64エンコード
        const base64Image = reader.result;

        // サーバーにPOSTリクエストを送信
        const formData = new FormData();
        formData.append("image", base64Image);

        fetch("/ocr", {
            method: "POST",
            body: formData,
        })
        .then(response => {
            if (response.ok) {
                // OCR処理が完了したら結果ページにリダイレクト
                window.location.href = "/result";  // 結果ページにリダイレクト
            } else {
                alert("エラーが発生しました");
            }
        })
        .catch(error => {
            alert("エラーが発生しました");
        });
    };

    reader.readAsDataURL(file);  // 画像ファイルをBase64エンコード
}