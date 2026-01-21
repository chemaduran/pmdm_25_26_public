	private fun copyFileToInternalStorage() {
		try {
			val fileName = "contactos.csv"
			val file = File(filesDir, fileName)

			// Solo copiar si no existe
			if (!file.exists()) {
				// Leer desde res/raw o assets
				val inputStream = assets.open(fileName)
				val outputStream = openFileOutput(fileName, MODE_PRIVATE)

				inputStream.copyTo(outputStream)
				inputStream.close()
				outputStream.close()
			}
		} catch (e: Exception) {
			Log.e("MainActivity", "Error copying file: ${e.message}")
		}
	}