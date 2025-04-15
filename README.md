# Photos30
Photo Assignment for CS213  
Sarpreet Singh NetId: ss4426  
Three Peng NetID: sp2269  
Good luck on project

This is a single-user photo management application built with JavaFX and FXML. The app allows users to organize photos into albums, tag and search them, and save their data persistently. All user-facing interaction is implemented in a graphical user interface using JavaFX.

---

## 🔐 Login System

When the application starts:

- Login using a **username** (no password required).
- Special usernames:
  - `admin` → Opens the admin subsystem.
  - `stock` → Loads preloaded stock photos.
- Any other username creates or loads a non-admin user.

---

## 🛠 Admin Subsystem

Available when logged in as `admin`.

### Features:
- **Create user** – Add a new user account.
- **Delete user** – Remove a user (except `admin` or `stock`).
- **List users** – Displays all existing usernames.

---

## 👤 Non-Admin User Subsystem

Once logged in as a regular user, the following features are available:

---

### 📂 Album Management

- **Open Album** – Shows a list of your albums with:
  - Name
  - Number of photos
  - Earliest & latest photo dates
- **Search** - Search for a photo.
- **Create Album** – Add a new album.
- **Rename Album** – Change album name.
- **Delete Album** – Remove an existing album.
- **Logout** - Logout to the login page.

---

### 🖼 Photo Management

Inside any album:

- **Open Photo** – Opens a detailed viewer with:
  - Thumbnail image
  - Caption
  - Date (based on last file modification)
  - Tags
  - Move Photo – Transfers photo to a different album.
  - Copy Photo – Adds photo to another album without removing it.
  - Slideshow Navigation – Use arrows to view next/previous photos.
- **Add Photo** – Import a photo from the file system.
  - If the photo already exists in your library, it’s reused.
  - Caption is prompted when the photo is first added.
- **Delete Photo** – Deletes photo from that album only.
- **Rename Photo** – Edit the photo’s caption.

---



### 🔖 Tagging

- **Add Tags** – Create tag types and assign values to photos.
  - Supports both single-value and multi-value tags.
  - Example: `location=Paris`, `person=John, Jane`
  - Multi-value tags can be used and split with a comma
- **Edit Tags** – Modify existing tag values.
- **Delete Tags** – Remove a tag from a photo.
- Tags are shared across all albums referencing the same photo.

---

## 🔍 Search Functionality

Click “Search Photos” from the main screen.

### Options:
- **Search by Date Range**
  - Select a start and end date.
- **Search by Tags**
  - Single tag: `person=Alice`
  - Two tags:
    - `AND` logic: `person=Alice AND location=Paris`
    - `OR` logic: `person=Alice OR person=Bob`
- **Save Results**
  - Search results can be saved as a new album (with shared photo references).

---

## 🖼 Stock Photos

- The app auto-generates a `stock` user on first run.
- It loads images from the `/resources/stock/` folder into a "stock" album.
- To access, login as `stock`.

---

## 💾 Data Persistence

- All data (users, albums, photos, tags) is saved in the `/data/` folder.
- Data is stored using Java serialization as `<username>.user`.
- Data is auto-saved on logout or window close.

---

## ⚙️ Technical Notes

- Built using Java 21 and JavaFX 21.
- All UI screens are constructed using FXML.
- Models implement `Serializable` for persistence.
- Shared photo references ensure consistency across albums.

---

## 📌 Assignment Features Checklist

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
