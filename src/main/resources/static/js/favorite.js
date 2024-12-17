document.addEventListener('DOMContentLoaded', () => {
    const openPopupButton = document.getElementById('openPopupButton');
    const closePopupButton = document.getElementById('closePopupButton');
    const popup = document.getElementById('popup');

    // ポップアップを表示
    openPopupButton.addEventListener('click', () => {
        popup.classList.remove('hidden');
    });

    // ポップアップを閉じる
    closePopupButton.addEventListener('click', () => {
        popup.classList.add('hidden');
    });

    // 背景をクリックして閉じる
    popup.addEventListener('click', (event) => {
        if (event.target === popup) {
            popup.classList.add('hidden');
        }
    });
});
