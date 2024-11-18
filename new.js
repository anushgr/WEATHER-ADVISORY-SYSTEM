const multer = require('multer');
const path = require('path');

const storage = multer.diskStorage({
    destination: (req, file, cb) => cb(null, 'uploads/'),
    filename: (req, file, cb) => cb(null, `${Date.now()}-${file.originalname}`),
});
const upload = multer({ storage });

app.post('/upload', upload.single('file'), (req, res) => {
    res.json({ fileUrl: `http://localhost:5000/uploads/${req.file.filename}` });
});

app.use('/uploads', express.static(path.join(__dirname, 'uploads')));
