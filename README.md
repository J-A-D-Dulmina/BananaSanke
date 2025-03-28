# 🍌 BananaSanke - A Simple Math Puzzle Snake Game

BananaSanke is a fun and educational game that combines the classic snake game with math puzzles. The game is developed in **Java** using **Eclipse** and **Java SE 1.8**. The server-side is hosted on a WordPress hosting environment, while the game itself is a standalone application. The project follows the **MVC (Model-View-Controller)** architecture to ensure modularity, maintainability, and scalability.

---

## ✨ Features
- 🎮 **Math Puzzle Gameplay**: Solve math puzzles while playing the classic snake game.
- 🏆 **Score System**: Earn points by eating the correct answers.
- 💻 **Standalone Application**: The game runs independently on your system.
- 🌐 **Server-Side Integration**: Handles user accounts, leaderboards, and email notifications.
- 🔒 **Secure Authentication**: Uses **JWT tokens** for secure user authentication.
- 📧 **Email Notifications**: Sends password reset codes and account-related notifications using **PHP Mailer**.
- 🛠️ **Low Coupling and High Cohesion**: Implements **interfaces** to ensure modularity and reduce dependencies between components.
- 🖥️ **Cross-Platform Support**: Compatible with **Windows**, **Ubuntu**, and other **Linux** distributions.

---

## 🚀 How to Run the Game

### Option 1: Run the Executable (Windows Only)
1. **Download the Executable**:
   - Go to the [Releases](https://github.com/J-A-D-Dulmina/BananaSanke/releases) section of this repository.
   - Download the latest `.exe` file.
2. **Read the README**:
   - Follow the instructions in the README file included with the executable.
3. **Play the Game**:
   - Run the `.exe` file and enjoy the game!

---

### Option 2: Run the Code in Eclipse (Windows, Ubuntu, and Linux)
1. **Download the Source Code**:
   - Clone or download the repository to your local machine.

2. **Open in Eclipse**:
   - Open Eclipse and go to **File > Import > Existing Projects into Workspace**.
   - Select the folder where you downloaded the source code and click **Finish**.

3. **Add External Libraries**:
   - The project includes all required external libraries (e.g., for JSON parsing, JWT, etc.) in the `libraries/` folder.
   - To add these libraries to the project:
     - Right-click on the project in Eclipse and select **Build Path > Configure Build Path**.
     - Go to the **Libraries** tab and click **Add JARs**.
     - Navigate to the `libraries/` folder in the project directory.
     - Select all the JAR files in the `libraries/` folder and click **OK**.
     - Click **Apply and Close**.

4. **Run the Game**:
   - Locate the `main` class in the project (e.g., `Main.java`).
   - Right-click on the file and select **Run As > Java Application**.

---

## 🖥️ Cross-Platform Support

### Windows
- Download the `.exe` file from the [Releases](https://github.com/J-A-D-Dulmina/BananaSanke/releases) section.
- Alternatively, run the source code in Eclipse as described above.

### Ubuntu and Linux
1. Ensure **Java SE 1.8** or higher is installed:
   ```bash
   sudo apt update
   sudo apt install openjdk-8-jdk
   ```

---

# Interface Screenshots


<div align="center">
  <table>
    <tr>   
      <td align="center"><img src="https://github.com/user-attachments/assets/c3dbe2ca-2db4-4a6c-ab5e-73b85ff0da34" alt="2"></td>
      <td align="center"><img src="https://github.com/user-attachments/assets/1997e6d7-b172-46de-864d-97a124f57ac5" alt="3"></td>
      <td align="center"><img src="https://github.com/user-attachments/assets/de8eb2a0-ed40-48bf-b90d-4a2eb4d068fa" alt="4"></td>
    </tr>
    <tr>
      <td align="center"><img src="https://github.com/user-attachments/assets/2287dd7c-031b-4b2a-96de-8cbace1d7801" alt="1"></td>
      <td align="center"><img src="https://github.com/user-attachments/assets/9ac1788f-c2d1-4ed8-8aa5-13c5df9e3a35" alt="8"></td>
      <td align="center"><img src="https://github.com/user-attachments/assets/e8c53de8-1ca8-4435-af33-bda7b546b1c1" alt="9"></td>
    </tr>
    <tr>
    <td align="center"><img src="https://github.com/user-attachments/assets/71083cf7-5f91-4aa5-b9a5-323c0d519776" alt="10"></td>
      <td align="center"><img src="https://github.com/user-attachments/assets/80962da8-fe63-46dc-879e-2e7f8325b6b3" alt="7"></td>
      <td align="center"><img src="https://github.com/user-attachments/assets/07216f6f-bbb5-47cb-8f5b-a6ec0ab607d5" alt="6"></td>
    </tr>
    <tr>
      <td></td>
      <td align="center"><img src="https://github.com/user-attachments/assets/149a2897-9b9d-4b85-82ab-30882f838a76" alt="5"></td>
      <td></td>
    </tr>
  </table>
</div>
