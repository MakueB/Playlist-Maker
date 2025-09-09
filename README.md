# Playlist Maker

<a href="https://github.com/MakueB/Playlist-Maker"><img src="https://img.shields.io/badge/Platform-Android-green.svg" alt="Platform"></a>
<a href="https://github.com/MakueB/Playlist-Maker/blob/master/LICENSE"><img src="https://img.shields.io/github/license/MakueB/Playlist-Maker" alt="License"></a>
<a href="https://kotlinlang.org/"><img src="https://img.shields.io/badge/Kotlin-100%25-purple.svg" alt="Kotlin"></a>

**Playlist Maker** — это современное Android-приложение для поиска музыки и создания персональных плейлистов с использованием официального API iTunes Search. Пользователи могут искать треки, прослушивать превью, добавлять понравившиеся композиции в свой плейлист и делиться им.

<p align="center">
  <img src="https://github.com/MakueB/Playlist-Maker/raw/master/art/screen_1.jpg" width="30%" alt="Search Screen"/>
  <img src="https://github.com/MakueB/Playlist-Maker/raw/master/art/screen_2.jpg" width="30%" alt="Playlist Screen"/>
</p>

## 🚀 Возможности

*   **Поиск музыки:** Интеграция с iTunes Search API для поиска треков по названию, артисту или альбому.
*   **Просмотр превью:** Воспроизведение 30-секундных preview-версий треков прямо в приложении.
*   **Создание плейлистов:** Добавление и удаление треков в персональный плейлист.
*   **Управление медиа:** Удобный медиа-плеер с кнопками play/pause для контроля воспроизведения.
*   **Делиться плейлистом:** Экспорт плейлиста в виде текстового списка для обмена в мессенджерах и социальных сетях.

## 🛠 Стек технологий

*   **Язык:** [Kotlin](https://kotlinlang.org/) - 100%
*   **Архитектура:** [MVVM](https://developer.android.com/topic/architecture) (Model-View-ViewModel) + Clean Architecture
*   **Асинхронность:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-guide.html) с [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/)
*   **DI (Внедрение зависимостей):** [Koin](https://insert-koin.io/)
*   **Сетевые запросы:** [Retrofit 2](https://square.github.io/retrofit/) с [Moshi](https://github.com/square/moshi)
*   **Воспроизведение аудио:** [Android MediaPlayer](https://developer.android.com/guide/topics/media/mediaplayer)
*   **Работа с данными:** [Room Database](https://developer.android.com/training/data-storage/room) для кеширования треков

## 📦 Установка и запуск

1.  Склонируйте репозиторий:
    ```bash
    git clone https://github.com/MakueB/Playlist-Maker.git
    ```
2.  Откройте проект в **Android Studio** (версия Arctic Fox или новее).
3.  Дождитесь завершения сборки и синхронизации Gradle.
4.  Соберите и запустите приложение на подключенном устройстве или эмуляторе (требуется API 21+).

## 🏗 Структура проекта

Проект следует принципам Clean Architecture и MVVM для обеспечения чистоты, тестируемости и масштабируемости кода.

## 📄 Лицензия

Этот проект распространяется под лицензией MIT. Подробнее см. в файле [LICENSE](LICENSE).

---
<div align="center">
Разработано с ❤️ от <a href="https://github.com/MakueB">MakueB</a>
</div>
