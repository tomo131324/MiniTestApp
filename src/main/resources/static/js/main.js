// ロゴをクリックしたときにサイドバーを開閉するJavaScript
document.getElementById('logo').addEventListener('click', function () {
    var sidebar = document.getElementById('sidebar');
    sidebar.classList.toggle('open');
});

// 削除確認js
var modal = document.getElementById('deleteModal');
var confirmDeleteBtn = document.getElementById('confirmDelete');
var cancelDeleteBtn = document.getElementById('cancelDelete');

// 各削除ボタンにクリックイベントを追加
var deleteBtns = document.querySelectorAll('.delete-btn');
deleteBtns.forEach(function(btn) {
  btn.addEventListener('click', function(event) {
    event.preventDefault(); // デフォルトのリンク動作を防ぐ
    modal.style.display = 'block'; // モーダルを表示
    var deleteUrl = this.getAttribute('href'); // 削除リンクのURLを取得
    confirmDeleteBtn.onclick = function() {
      window.location.href = deleteUrl; // 確認ボタンで削除実行
    };
  });
});

// キャンセルボタンでモーダルを非表示
cancelDeleteBtn.onclick = function() {
  modal.style.display = 'none';
};


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
    if (file.size > 20 * 1024 * 1024) { // 20MB
        alert('ファイルサイズが大きすぎます。20MB以下の画像を使用してください。');
        event.target.value = ''; // ファイル選択をクリア
        location.reload();
        return;
    }

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



