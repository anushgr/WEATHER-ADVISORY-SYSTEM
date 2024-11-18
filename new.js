const multer = require('multer');
const path = require('path');

const storage = multer.diskStorage({


app.use('/uploads', express.static(path.join(__dirname, 'uploads')));
