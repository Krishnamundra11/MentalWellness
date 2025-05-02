const admin = require('firebase-admin');
const serviceAccount = require('./serviceAccountKey.json'); // You'll need to download this from Firebase console

// Initialize Firebase Admin SDK
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();
const userId = 'hZTyOD0a7TVV9LtCZ6dCLjMgmxH3';

// Helper function to generate random integer between min and max (inclusive)
function getRandomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

// Helper function to format date as YYYY-MM-DD
function formatDate(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

// Generate answers for questionnaires
function generateAnswers() {
  const options = ['Excellent', 'Good', 'Okay', 'Poor', 'Very Poor'];
  const answers = {};
  
  for (let i = 1; i <= 5; i++) {
    // Randomly select one of the 5 options for each question
    const randomIndex = getRandomInt(0, 4);
    answers[`q${i}`] = options[randomIndex];
  }
  
  return answers;
}

// Generate recommendations based on category and score
function generateRecommendations(category, score) {
  const recommendations = {
    stress: {
      low: [
        'Breathe deeply, relax completely.',
        'Move your body, clear your mind.',
        'Smile more, stress less.',
        'Pause, reflect, let go.'
      ],
      medium: [
        'Take short breaks during work.',
        'Practice mindfulness for 5 minutes daily.',
        'Set realistic goals for yourself.',
        'Connect with friends regularly.'
      ],
      high: [
        'Prioritize sleep and nutrition.',
        'Consider talking to a counselor.',
        'Limit caffeine and screen time.',
        'Try guided meditation apps.'
      ]
    },
    academic: {
      low: [
        'Create a study schedule.',
        'Find a distraction-free environment.',
        'Take better notes in class.',
        'Join a study group.'
      ],
      medium: [
        'Review material regularly.',
        'Ask questions when confused.',
        'Take short breaks while studying.',
        'Use active recall techniques.'
      ],
      high: [
        'Keep up your excellent focus!',
        'Continue your consistent study habits.',
        'Maintain your positive academic attitude.',
        'Share your study techniques with peers.'
      ]
    },
    sleep: {
      low: [
        'Maintain consistent sleep/wake times.',
        'Create a restful bedroom environment.',
        'Avoid screens 1 hour before bed.',
        'Try relaxation techniques before sleeping.'
      ],
      medium: [
        'Limit daytime naps to 20 minutes.',
        'Exercise regularly but not before bed.',
        'Avoid large meals and caffeine before sleep.',
        'Create a bedtime routine.'
      ],
      high: [
        'Keep your quality sleep routine.',
        'Continue limiting screen time before bed.',
        'Maintain your consistent sleep schedule.',
        'Share your healthy sleep habits.'
      ]
    }
  };

  let range;
  if (score < 40) range = 'low';
  else if (score < 75) range = 'medium';
  else range = 'high';

  return recommendations[category][range];
}

// Create data for the past 3 weeks (from April 20th to today)
async function generateDataForPastThreeWeeks() {
  // Start date: April 20, 2025
  const startDate = new Date(2025, 3, 20); // Month is 0-indexed in JavaScript
  const endDate = new Date(); // Today
  
  // Set today's date to 2025 to match your Firestore screenshot
  endDate.setFullYear(2025);
  
  const categories = ['stress', 'academic', 'sleep'];
  const promises = [];

  // Loop through each day from start date to end date
  for (let date = new Date(startDate); date <= endDate; date.setDate(date.getDate() + 1)) {
    const dateStr = formatDate(date);
    
    // For each day, create entries for all three categories
    for (const category of categories) {
      // Generate a random score between 20 and 95
      const score = getRandomInt(20, 95);
      
      // Generate random answers for 5 questions
      const answers = generateAnswers();
      
      // Generate appropriate recommendations based on score
      const recommendations = generateRecommendations(category, score);
      
      // Create document data
      const docData = {
        category: category,
        date: dateStr,
        score: score,
        answers: answers,
        recommendations: recommendations
      };
      
      // Document ID format: category-date (e.g., stress-2025-05-02)
      const docId = `${category}-${dateStr}`;
      
      // Add to Firestore
      const docRef = db.collection('users').doc(userId).collection('questionnaires').doc(docId);
      promises.push(docRef.set(docData));
      
      console.log(`Creating document: ${docId}`);
    }
  }
  
  // Wait for all documents to be written
  await Promise.all(promises);
  console.log('All data has been written successfully!');
  
  // Terminate the Firebase Admin SDK
  admin.app().delete();
}

// Execute the function
generateDataForPastThreeWeeks().catch(error => {
  console.error('Error generating data:', error);
  process.exit(1);
}); 