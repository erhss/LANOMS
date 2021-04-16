import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javafx.scene.image.Image;

public class User implements Serializable, Comparable<User> {
		private String username;
		private String name;
		private String message;
		private String status;
		private String department;
		private String email;
		private Image displayPicture;
		
		public User(String username, String name, String message, String status, String department, String email, Image displayPicture) {
			this.username = username;
			this.name = name;
			this.message = message;
			this.status = status;
			this.department = department;
			this.email = email;
			this.displayPicture = displayPicture;
		}
		
		public void setMessage(String message) {
			this.message = message;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getUsername() {
			return username;
		}
		
		public String getName() {
			return name;
		}
		
		public String getMessage() {
			return message;
		}
		
		public String getStatus() {
			return status;
		}
		
		public String getDepartment() {
			return department;
		}
		
		public String getEmail() {
			return email;
		}
		
		public Image getDisplayPicture() {
			return displayPicture;
		}

		@Override
		public int compareTo(User obj) {
			return name.compareTo(obj.name);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof User)
				return ((User)obj).name.equals(name);
			
			return false;
		}
		
		
		public void saveUser() {
			try {
			File makedir = new File(".\\UserSettings");
			boolean dirCreated = makedir.mkdirs();
			User tempUser = new User(this.username = username,
			this.name = name,
			this.message = message,
			this.status = status,
			this.department = department,
			this.email = email,
			this.displayPicture = displayPicture);
			
			 FileOutputStream fileName = new FileOutputStream(makedir + "\\" + this.username+".lanoms"); 
	         ObjectOutputStream out = new ObjectOutputStream(fileName);
	         out.writeObject(tempUser);
	         out.close();
	         fileName.close();
	         System.out.println("Account saved!");
	      } catch (IOException e) {
	         e.printStackTrace();
	      }
		}
		
	}