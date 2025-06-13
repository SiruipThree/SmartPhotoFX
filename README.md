# SmartPhotoFX

A JavaFX-based desktop application for managing personal photo collections with multi-user support, tagging, search, and album organization. This project was developed to demonstrate advanced proficiency in Java GUI design, object-oriented modeling, and persistent data management.

---

## 🔐 Login System

- Users log in with a **username** (no password).
- Special usernames:
  - `admin` → Opens the admin dashboard.
  - `stock` → Loads preloaded sample photos.
- Any other username creates or loads a regular user profile.

---

## 🛠 Admin Dashboard

Accessible via the `admin` account.

### Features:
- **Create User**
- **Delete User**
- **List All Users**

---

## 👤 Standard User Interface

### 📂 Album Management

- View album list with:
  - Name
  - Photo count
  - Date range
- Create, rename, and delete albums
- Search across albums
- Logout option

### 🖼 Photo Management

- View photos with:
  - Thumbnails
  - Captions
  - Last modified date
  - Tags
- Add, rename, delete, copy, or move photos
- Slideshow navigation

---

### 🔖 Tagging System

- Add/edit/delete custom tags
- Supports single- and multi-value tags (e.g., `person=John, Jane`)
- Tags persist across albums referencing the same photo

---

## 🔍 Photo Search

- **By Date Range** – Select a start and end date
- **By Tags**
  - Single tag: `location=Paris`
  - AND/OR logic for dual-tag queries
- **Save Results** as a new album

---

## 🖼 Stock Album

- Automatically generates on first run
- Loads sample images from the `/resources/stock/` folder

---

## 💾 Data Persistence

- All data stored in `/data/` as serialized objects
- Auto-saved on logout or exit
- Supports loading previous sessions

---

## ⚙️ Technical Overview

- Java 21 + JavaFX 21
- UI built with FXML
- Follows MVC architecture
- Java `Serializable` model classes
- Folder Structure:
  - `src/` – Java source code
  - `resources/` – FXML and assets
  - `out/` – Compiled output

### 🔧 Compilation & Run (Sample for Local Machine)

```bash
# Copy FXML/resources
cp -r resources out/

# Compile source
javac --module-path /path/to/javafx-sdk/lib \
      --add-modules javafx.controls,javafx.fxml \
      -d out src/application/Photos.java \
      src/application/controller/*.java \
      src/application/model/*.java

# Run app
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -cp out application.Photos


## 📌 Features Checklist

| Feature                                  | Implemented |
|------------------------------------------|-------------|
| Admin create/delete users                | ✅          |
| Album CRUD                               | ✅          |
| Add/view/caption/delete photos           | ✅          |
| Shared photo object across albums        | ✅          |
| Add/delete/edit tag values               | ✅          |
| Tag types with single/multiple values    | ✅          |
| Search by date range                     | ✅          |
| Search by tag (single AND/OR double)     | ✅          |
| Save search results as album             | ✅          |
| Stock user and preloaded album           | ✅          |
| JavaFX UI via FXML                       | ✅          |
| Model-View-Controller structure          | ✅          |
| Java serialization for saving data       | ✅          |
| Error handling through GUI alerts        | ✅          |

---

The final version can be found on the main branch.

Thank you for reviewing the project!
