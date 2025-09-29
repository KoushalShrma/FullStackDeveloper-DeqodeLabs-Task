#!/bin/bash

echo "🚀 Preparing Finance Research Chatbot for Deployment..."

# Backend preparation
echo "📦 Building backend..."
cd backend
mvn clean package -DskipTests
echo "✅ Backend built successfully!"

# Frontend preparation  
echo "📦 Building frontend..."
cd ../frontend
npm install
npm run build
echo "✅ Frontend built successfully!"

cd ..

echo "🎉 Build complete! Ready for deployment."
echo ""
echo "Next steps:"
echo "1. Push to GitHub: git add . && git commit -m 'Ready for deployment' && git push"
echo "2. Deploy backend to Railway: Connect GitHub repo to Railway"
echo "3. Deploy frontend to Netlify: Connect GitHub repo to Netlify"
echo "4. Configure environment variables as per DEPLOYMENT.md"
echo ""
echo "📖 See DEPLOYMENT.md for detailed instructions"