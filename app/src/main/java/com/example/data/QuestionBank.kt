package com.example.data

import com.example.data.entity.Question

object QuestionBank {
    val questions: List<Question> = listOf(
        // ==========================================
        // COUNTRIES
        // ==========================================
        // --- KIDS (Ages 5-8) ---
        Question(
            category = "COUNTRIES", ageDivision = "KIDS",
            text = "Which animal is famous in Australia and hops around carrying its baby in a pouch?",
            optionA = "Lion", optionB = "Kangaroo", optionC = "Panda", optionD = "Elephant",
            correctOption = "B", explanation = "Kangaroos are native to Australia and are famous for hopping and carrying their babies (joeys) in pouches."
        ),
        Question(
            category = "COUNTRIES", ageDivision = "KIDS",
            text = "What is the capital city of the United States of America?",
            optionA = "New York", optionB = "Los Angeles", optionC = "Washington, D.C.", optionD = "Chicago",
            correctOption = "C", explanation = "Washington, D.C. is the capital city of the United States."
        ),
        Question(
            category = "COUNTRIES", ageDivision = "KIDS",
            text = "Which country has the famous Great Wall, which is so long it can be seen from space?",
            optionA = "India", optionB = "Japan", optionC = "China", optionD = "Egypt",
            correctOption = "C", explanation = "The Great Wall of China is one of the world's most famous structures and is located in China."
        ),
        Question(
            category = "COUNTRIES", ageDivision = "KIDS",
            text = "Which country is shaped like a giant boot in Europe?",
            optionA = "France", optionB = "Italy", optionC = "Spain", optionD = "Germany",
            correctOption = "B", explanation = "Italy has a very distinctive shape on the map that looks exactly like a high-heeled boot!"
        ),
        Question(
            category = "COUNTRIES", ageDivision = "KIDS",
            text = "Where would you go to see the ancient Pyramids and the Sphinx?",
            optionA = "Egypt", optionB = "Brazil", optionC = "Canada", optionD = "Mexico",
            correctOption = "A", explanation = "The Great Pyramids and the Sphinx are located in Egypt, in North Africa."
        ),

        // --- JUNIORS (Ages 9-12) ---
        Question(
            category = "COUNTRIES", ageDivision = "JUNIORS",
            text = "Which is the largest country in the world by land area?",
            optionA = "Canada", optionB = "China", optionC = "United States", optionD = "Russia",
            correctOption = "D", explanation = "Russia is the largest country in the world, spanning across eastern Europe and northern Asia."
        ),
        Question(
            category = "COUNTRIES", ageDivision = "JUNIORS",
            text = "Which capital city is known for its beautiful Eiffel Tower?",
            optionA = "London", optionB = "Paris", optionC = "Rome", optionD = "Berlin",
            correctOption = "B", explanation = "The Eiffel Tower is located in Paris, the capital city of France."
        ),
        Question(
            category = "COUNTRIES", ageDivision = "JUNIORS",
            text = "In which continent is the Amazon Rainforest primarily located?",
            optionA = "Africa", optionB = "Asia", optionC = "South America", optionD = "Australia",
            correctOption = "C", explanation = "The Amazon Rainforest is located in South America, with the largest portion of it in Brazil."
        ),
        Question(
            category = "COUNTRIES", ageDivision = "JUNIORS",
            text = "Which country is also a continent and is completely surrounded by oceans?",
            optionA = "Greenland", optionB = "Australia", optionC = "Madagascar", optionD = "Japan",
            correctOption = "B", explanation = "Australia is unique because it is both a country and an entire continent, often called an island continent."
        ),
        Question(
            category = "COUNTRIES", ageDivision = "JUNIORS",
            text = "What is the capital city of Japan, which is also one of the most populated cities in the world?",
            optionA = "Beijing", optionB = "Seoul", optionC = "Tokyo", optionD = "Bangkok",
            correctOption = "C", explanation = "Tokyo is the capital city of Japan and has a vibrant mix of modern and traditional culture."
        ),

        // --- SENIORS (Ages 13+) ---
        Question(
            category = "COUNTRIES", ageDivision = "SENIORS",
            text = "Which European country is bordered by Spain to its west and France to its northeast?",
            optionA = "Portugal", optionB = "Andorra", optionC = "Italy", optionD = "Morocco",
            correctOption = "B", explanation = "Andorra is a tiny, landlocked microstate situated in the Pyrenees mountains between France and Spain."
        ),
        Question(
            category = "COUNTRIES", ageDivision = "SENIORS",
            text = "Which country has the highest number of active volcanoes in the world?",
            optionA = "Indonesia", optionB = "Japan", optionC = "Iceland", optionD = "United States",
            correctOption = "A", explanation = "Indonesia sits on the 'Ring of Fire' and has over 120 active volcanoes, the most in the world."
        ),
        Question(
            category = "COUNTRIES", ageDivision = "SENIORS",
            text = "What is the capital of Canada?",
            optionA = "Toronto", optionB = "Vancouver", optionC = "Montreal", optionD = "Ottawa",
            correctOption = "D", explanation = "While Toronto is the largest city, Ottawa is the official federal capital of Canada."
        ),
        Question(
            category = "COUNTRIES", ageDivision = "SENIORS",
            text = "Which of the following countries is landlocked (completely surrounded by land)?",
            optionA = "Vietnam", optionB = "Bolivia", optionC = "Argentina", optionD = "South Africa",
            correctOption = "B", explanation = "Bolivia is a landlocked country in South America, having lost its coastline to Chile in the late 19th century."
        ),
        Question(
            category = "COUNTRIES", ageDivision = "SENIORS",
            text = "Which country's flag features a red maple leaf in the center?",
            optionA = "Canada", optionB = "Lebanon", optionC = "Switzerland", optionD = "Peru",
            correctOption = "A", explanation = "Canada's national flag, also known as the Maple Leaf, features a red maple leaf with 11 points."
        ),

        // ==========================================
        // SCIENCE
        // ==========================================
        // --- KIDS (Ages 5-8) ---
        Question(
            category = "SCIENCE", ageDivision = "KIDS",
            text = "Which giant star shines brightly during the day and gives heat to the Earth?",
            optionA = "The Moon", optionB = "The Sun", optionC = "Mars", optionD = "Polaris",
            correctOption = "B", explanation = "The Sun is a yellow dwarf star at the center of our solar system that provides light and heat to Earth."
        ),
        Question(
            category = "SCIENCE", ageDivision = "KIDS",
            text = "What is ice made of?",
            optionA = "Sugar", optionB = "Water", optionC = "Milk", optionD = "Salt",
            correctOption = "B", explanation = "Ice is water in its solid state, which happens when water is frozen below 0°C (32°F)."
        ),
        Question(
            category = "SCIENCE", ageDivision = "KIDS",
            text = "How many legs does a spider have?",
            optionA = "6 legs", optionB = "8 legs", optionC = "10 legs", optionD = "4 legs",
            correctOption = "B", explanation = "Spiders are arachnids, meaning they have 8 legs, unlike insects which have 6 legs."
        ),
        Question(
            category = "SCIENCE", ageDivision = "KIDS",
            text = "Which of these is NOT a planet in our solar system?",
            optionA = "Earth", optionB = "Jupiter", optionC = "The Moon", optionD = "Mars",
            correctOption = "C", explanation = "The Moon is Earth's natural satellite, not a planet."
        ),
        Question(
            category = "SCIENCE", ageDivision = "KIDS",
            text = "What pulls apples down from trees to the ground?",
            optionA = "Wind", optionB = "Gravity", optionC = "Magnets", optionD = "Sunlight",
            correctOption = "B", explanation = "Gravity is the invisible force that pulls objects toward each other, causing things to fall to the earth."
        ),

        // --- JUNIORS (Ages 9-12) ---
        Question(
            category = "SCIENCE", ageDivision = "JUNIORS",
            text = "What gas do humans and animals need to inhale from the air to survive?",
            optionA = "Carbon Dioxide", optionB = "Nitrogen", optionC = "Oxygen", optionD = "Helium",
            correctOption = "C", explanation = "Humans and animals need oxygen to breathe and power their bodies' cells."
        ),
        Question(
            category = "SCIENCE", ageDivision = "JUNIORS",
            text = "Which organ inside your chest pumps blood throughout your entire body?",
            optionA = "Lungs", optionB = "Stomach", optionC = "Brain", optionD = "Heart",
            correctOption = "D", explanation = "The heart is a strong muscular organ that acts as a continuous pump for blood circulation."
        ),
        Question(
            category = "SCIENCE", ageDivision = "JUNIORS",
            text = "How long does it take for the Earth to orbit once around the Sun?",
            optionA = "24 Hours", optionB = "30 Days", optionC = "365 Days", optionD = "10 Years",
            correctOption = "C", explanation = "It takes approximately 365.25 days (one year) for the Earth to complete a full revolution around the Sun."
        ),
        Question(
            category = "SCIENCE", ageDivision = "JUNIORS",
            text = "What is the boiling point of pure water in Celsius?",
            optionA = "50°C", optionB = "100°C", optionC = "0°C", optionD = "200°C",
            correctOption = "B", explanation = "At standard atmospheric pressure, pure water boils and turns to steam at exactly 100°C."
        ),
        Question(
            category = "SCIENCE", ageDivision = "JUNIORS",
            text = "What process do green plants use to make food using sunlight, water, and air?",
            optionA = "Photosynthesis", optionB = "Respiration", optionC = "Metamorphosis", optionD = "Evaporation",
            correctOption = "A", explanation = "Photosynthesis is how plants convert light energy into chemical energy (food) using chlorophyll."
        ),

        // --- SENIORS (Ages 13+) ---
        Question(
            category = "SCIENCE", ageDivision = "SENIORS",
            text = "What is the chemical symbol for gold on the periodic table?",
            optionA = "Go", optionB = "Gd", optionC = "Au", optionD = "Ag",
            correctOption = "C", explanation = "The chemical symbol for Gold is Au, derived from the Latin word 'aurum' meaning shining dawn."
        ),
        Question(
            category = "SCIENCE", ageDivision = "SENIORS",
            text = "Which of Newton's Laws states that 'For every action, there is an equal and opposite reaction'?",
            optionA = "First Law", optionB = "Second Law", optionC = "Third Law", optionD = "Law of Gravitation",
            correctOption = "C", explanation = "Newton's Third Law of Motion states that forces always occur in matched action-reaction pairs."
        ),
        Question(
            category = "SCIENCE", ageDivision = "SENIORS",
            text = "Which component of the blood is primarily responsible for fighting infections?",
            optionA = "Red Blood Cells", optionB = "White Blood Cells", optionC = "Platelets", optionD = "Plasma",
            correctOption = "B", explanation = "White blood cells (leukocytes) are part of the immune system and defend the body against infectious diseases."
        ),
        Question(
            category = "SCIENCE", ageDivision = "SENIORS",
            text = "What is the main gas that makes up the atmosphere of the planet Venus?",
            optionA = "Oxygen", optionB = "Nitrogen", optionC = "Carbon Dioxide", optionD = "Methane",
            correctOption = "C", explanation = "Venus has a thick, runaway greenhouse atmosphere comprised of over 96% Carbon Dioxide."
        ),
        Question(
            category = "SCIENCE", ageDivision = "SENIORS",
            text = "In genetics, what shape is a molecule of DNA described as?",
            optionA = "Single Sphere", optionB = "Double Helix", optionC = "Triple Ring", optionD = "Flat Hexagon",
            correctOption = "B", explanation = "DNA consists of two strands twisting around each other, forming a double helix structure."
        ),

        // ==========================================
        // HISTORY
        // ==========================================
        // --- KIDS (Ages 5-8) ---
        Question(
            category = "HISTORY", ageDivision = "KIDS",
            text = "What massive creatures ruled the Earth millions of years ago before disappearing?",
            optionA = "Dinosaurs", optionB = "Mammoths", optionC = "Dragons", optionD = "Unicorns",
            correctOption = "A", explanation = "Dinosaurs were ancient reptiles that dominated the earth until a mass extinction event around 66 million years ago."
        ),
        Question(
            category = "HISTORY", ageDivision = "KIDS",
            text = "Who was the brave queen of Egypt famous for living in a golden palace?",
            optionA = "Queen Elizabeth", optionB = "Cleopatra", optionC = "Pocahontas", optionD = "Mulan",
            correctOption = "B", explanation = "Cleopatra VII was the famous last active ruler of the Ptolemaic Kingdom of Egypt."
        ),
        Question(
            category = "HISTORY", ageDivision = "KIDS",
            text = "What did early humans live in before they built wooden houses?",
            optionA = "Apartments", optionB = "Castles", optionC = "Caves", optionD = "Tents",
            correctOption = "C", explanation = "Early humans sought shelter from cold, rain, and wild animals inside natural caves."
        ),
        Question(
            category = "HISTORY", ageDivision = "KIDS",
            text = "Who is known as the inventor of the lightbulb?",
            optionA = "Thomas Edison", optionB = "Albert Einstein", optionC = "Isaac Newton", optionD = "Alexander Graham Bell",
            correctOption = "A", explanation = "Thomas Edison developed the first commercially practical incandescent lightbulb in 1879."
        ),
        Question(
            category = "HISTORY", ageDivision = "KIDS",
            text = "Which of these did knights use to protect themselves from swords in battles?",
            optionA = "T-shirts", optionB = "Metal Armour", optionC = "Leather coats", optionD = "Jeans",
            correctOption = "B", explanation = "Medieval knights wore heavy, custom-made suits of metal plates to block strikes."
        ),

        // --- JUNIORS (Ages 9-12) ---
        Question(
            category = "HISTORY", ageDivision = "JUNIORS",
            text = "Which ancient civilization built the magnificent Colosseum and invented concrete?",
            optionA = "Ancient Egyptians", optionB = "Ancient Greeks", optionC = "Ancient Romans", optionD = "The Mayans",
            correctOption = "C", explanation = "The Romans built massive engineering projects like the Colosseum using early recipes of concrete."
        ),
        Question(
            category = "HISTORY", ageDivision = "JUNIORS",
            text = "Who was the first person to step onto the surface of the Moon in 1969?",
            optionA = "Buzz Aldrin", optionB = "Neil Armstrong", optionC = "Yuri Gagarin", optionD = "John Glenn",
            correctOption = "B", explanation = "Neil Armstrong was the commander of Apollo 11 and the first human to walk on the moon."
        ),
        Question(
            category = "HISTORY", ageDivision = "JUNIORS",
            text = "Which ship, famously declared unsinkable, hit an iceberg and sank in 1912?",
            optionA = "The Mayflower", optionB = "The Titanic", optionC = "The Santa Maria", optionD = "The HMS Beagle",
            correctOption = "B", explanation = "The RMS Titanic sank in the North Atlantic Ocean on its maiden voyage after hitting an iceberg."
        ),
        Question(
            category = "HISTORY", ageDivision = "JUNIORS",
            text = "Which historical leader was a general and emperor of France who conquered much of Europe?",
            optionA = "Julius Caesar", optionB = "King Arthur", optionC = "Napoleon Bonaparte", optionD = "Alexander the Great",
            correctOption = "C", explanation = "Napoleon Bonaparte rose during the French Revolution and crowned himself Emperor of France."
        ),
        Question(
            category = "HISTORY", ageDivision = "JUNIORS",
            text = "What was the system of writing called in Ancient Egypt that used pictures instead of letters?",
            optionA = "Cuneiform", optionB = "Alphabet", optionC = "Hieroglyphics", optionD = "Runes",
            correctOption = "C", explanation = "Hieroglyphics used illustrative icons and symbols to represent syllables, words, or sounds."
        ),

        // --- SENIORS (Ages 13+) ---
        Question(
            category = "HISTORY", ageDivision = "SENIORS",
            text = "In which year did World War II end?",
            optionA = "1918", optionB = "1939", optionC = "1945", optionD = "1953",
            correctOption = "C", explanation = "World War II officially ended in September 1945 with the signing of surrender documents."
        ),
        Question(
            category = "HISTORY", ageDivision = "SENIORS",
            text = "Who wrote the famous US 'Declaration of Independence' in 1776?",
            optionA = "George Washington", optionB = "Thomas Jefferson", optionC = "Benjamin Franklin", optionD = "Abraham Lincoln",
            correctOption = "B", explanation = "Thomas Jefferson was the principal author of the Declaration of Independence."
        ),
        Question(
            category = "HISTORY", ageDivision = "SENIORS",
            text = "Which Roman Emperor crossed the Rubicon, declared himself dictator, and was assassinated on the Ides of March?",
            optionA = "Augustus", optionB = "Nero", optionC = "Julius Caesar", optionD = "Caligula",
            correctOption = "C", explanation = "Julius Caesar was assassinated by senators in 44 BC on March 15 (known as the Ides of March)."
        ),
        Question(
            category = "HISTORY", ageDivision = "SENIORS",
            text = "What historical period, translating to 'Rebirth', saw a major surge in art, science, and literature in Europe?",
            optionA = "The Enlightenment", optionB = "The Middle Ages", optionC = "The Renaissance", optionD = "The Industrial Revolution",
            correctOption = "C", explanation = "The Renaissance was a cultural revival spanning roughly the 14th to the 17th centuries."
        ),
        Question(
            category = "HISTORY", ageDivision = "SENIORS",
            text = "Which historical empire was ruled by Genghis Khan and became the largest contiguous land empire in history?",
            optionA = "Roman Empire", optionB = "Mongol Empire", optionC = "Ottoman Empire", optionD = "British Empire",
            correctOption = "B", explanation = "The Mongol Empire, founded by Genghis Khan in 1206, expanded across vast territories of Asia and Europe."
        ),

        // ==========================================
        // MATHS
        // ==========================================
        // --- KIDS (Ages 5-8) ---
        Question(
            category = "MATHS", ageDivision = "KIDS",
            text = "What is 5 + 3?",
            optionA = "7", optionB = "8", optionC = "9", optionD = "10",
            correctOption = "B", explanation = "If you count 5 items and add 3 more, you get exactly 8."
        ),
        Question(
            category = "MATHS", ageDivision = "KIDS",
            text = "If you have 10 candies and give 4 to your friend, how many candies do you have left?",
            optionA = "4", optionB = "5", optionC = "6", optionD = "7",
            correctOption = "C", explanation = "10 minus 4 equals 6 candies left."
        ),
        Question(
            category = "MATHS", ageDivision = "KIDS",
            text = "What shape has 3 straight sides and 3 corners?",
            optionA = "Square", optionB = "Triangle", optionC = "Circle", optionD = "Rectangle",
            correctOption = "B", explanation = "A triangle has three sides and three angles (or corners)."
        ),
        Question(
            category = "MATHS", ageDivision = "KIDS",
            text = "Which of these numbers is an even number?",
            optionA = "3", optionB = "5", optionC = "6", optionD = "7",
            correctOption = "C", explanation = "6 can be divided evenly into two groups of 3, making it an even number."
        ),
        Question(
            category = "MATHS", ageDivision = "KIDS",
            text = "What is double the number 4 (4 + 4)?",
            optionA = "6", optionB = "8", optionC = "10", optionD = "12",
            correctOption = "B", explanation = "Adding 4 to itself (4 + 4) equals 8."
        ),

        // --- JUNIORS (Ages 9-12) ---
        Question(
            category = "MATHS", ageDivision = "JUNIORS",
            text = "What is 12 multiplied by 8?",
            optionA = "86", optionB = "92", optionC = "96", optionD = "104",
            correctOption = "C", explanation = "12 * 8 = 96."
        ),
        Question(
            category = "MATHS", ageDivision = "JUNIORS",
            text = "What is the average of the numbers 10, 20, and 30?",
            optionA = "15", optionB = "20", optionC = "25", optionD = "30",
            correctOption = "B", explanation = "To find the average, sum the numbers (10+20+30=60) and divide by the count (3): 60 / 3 = 20."
        ),
        Question(
            category = "MATHS", ageDivision = "JUNIORS",
            text = "What is 3/4 represented as a decimal?",
            optionA = "0.25", optionB = "0.50", optionC = "0.75", optionD = "0.80",
            correctOption = "C", explanation = "Dividing 3 by 4 equals 0.75, which is seventy-five percent."
        ),
        Question(
            category = "MATHS", ageDivision = "JUNIORS",
            text = "If a square has a side length of 5 cm, what is its total area?",
            optionA = "20 sq cm", optionB = "25 sq cm", optionC = "15 sq cm", optionD = "30 sq cm",
            correctOption = "B", explanation = "The area of a square is calculated as side * side. 5 cm * 5 cm = 25 square centimeters."
        ),
        Question(
            category = "MATHS", ageDivision = "JUNIORS",
            text = "What is the next number in this pattern: 2, 4, 8, 16, ...?",
            optionA = "20", optionB = "24", optionC = "32", optionD = "64",
            correctOption = "C", explanation = "Each number in the sequence doubles. Double of 16 is 32."
        ),

        // --- SENIORS (Ages 13+) ---
        Question(
            category = "MATHS", ageDivision = "SENIORS",
            text = "Solve for x in the equation: 3x + 7 = 22",
            optionA = "3", optionB = "5", optionC = "6", optionD = "7",
            correctOption = "B", explanation = "Subtract 7 from both sides: 3x = 15. Divide by 3: x = 5."
        ),
        Question(
            category = "MATHS", ageDivision = "SENIORS",
            text = "What is the value of Pi (π) rounded to two decimal places?",
            optionA = "3.12", optionB = "3.14", optionC = "3.16", optionD = "3.18",
            correctOption = "B", explanation = "Pi is approximately equal to 3.14159..., which rounds to 3.14."
        ),
        Question(
            category = "MATHS", ageDivision = "SENIORS",
            text = "What is the square root of 225?",
            optionA = "13", optionB = "15", optionC = "17", optionD = "25",
            correctOption = "B", explanation = "15 multiplied by 15 is 225, so the square root is 15."
        ),
        Question(
            category = "MATHS", ageDivision = "SENIORS",
            text = "A box contains 3 red balls and 7 blue balls. What is the probability of randomly picking a red ball?",
            optionA = "30%", optionB = "70%", optionC = "3%", optionD = "50%",
            correctOption = "A", explanation = "There are 3 red balls out of 10 total balls. The probability is 3/10, which is 0.30 or 30%."
        ),
        Question(
            category = "MATHS", ageDivision = "SENIORS",
            text = "What is the value of 5 factorial (5!)?",
            optionA = "25", optionB = "60", optionC = "120", optionD = "150",
            correctOption = "C", explanation = "5! = 5 * 4 * 3 * 2 * 1 = 120."
        ),

        // ==========================================
        // GK (GENERAL KNOWLEDGE)
        // ==========================================
        // --- KIDS (Ages 5-8) ---
        Question(
            category = "GK", ageDivision = "KIDS",
            text = "How many colors are there in a standard rainbow?",
            optionA = "5 colors", optionB = "7 colors", optionC = "10 colors", optionD = "3 colors",
            correctOption = "B", explanation = "A rainbow has 7 colors: Red, Orange, Yellow, Green, Blue, Indigo, and Violet."
        ),
        Question(
            category = "GK", ageDivision = "KIDS",
            text = "Which animal is known as the 'King of the Jungle'?",
            optionA = "Tiger", optionB = "Lion", optionC = "Elephant", optionD = "Bear",
            correctOption = "B", explanation = "The lion is traditionally called the King of the Jungle due to its majestic roar, pride hierarchy, and hunting dominance."
        ),
        Question(
            category = "GK", ageDivision = "KIDS",
            text = "In which season do leaves fall off the trees and change color?",
            optionA = "Summer", optionB = "Autumn / Fall", optionC = "Spring", optionD = "Winter",
            correctOption = "B", explanation = "During Autumn or Fall, deciduous trees shed their leaves as temperatures drop."
        ),
        Question(
            category = "GK", ageDivision = "KIDS",
            text = "What instrument do you hit with wooden sticks to make beats?",
            optionA = "Flute", optionB = "Drums", optionC = "Guitar", optionD = "Piano",
            correctOption = "B", explanation = "Drums are percussion instruments that you strike with hands or sticks."
        ),
        Question(
            category = "GK", ageDivision = "KIDS",
            text = "Which of the following is a fruit, not a vegetable?",
            optionA = "Potato", optionB = "Apple", optionC = "Carrot", optionD = "Broccoli",
            correctOption = "B", explanation = "Apples grow on trees and contain seeds, making them botanical fruits, unlike potatoes which are underground tubers."
        ),

        // --- JUNIORS (Ages 9-12) ---
        Question(
            category = "GK", ageDivision = "JUNIORS",
            text = "Which is the largest ocean on the planet Earth?",
            optionA = "Atlantic Ocean", optionB = "Indian Ocean", optionC = "Pacific Ocean", optionD = "Arctic Ocean",
            correctOption = "C", explanation = "The Pacific Ocean is the largest and deepest of Earth's oceanic divisions."
        ),
        Question(
            category = "GK", ageDivision = "JUNIORS",
            text = "Who wrote the famous adventure book 'Harry Potter and the Sorcerer's Stone'?",
            optionA = "J.R.R. Tolkien", optionB = "C.S. Lewis", optionC = "J.K. Rowling", optionD = "Roald Dahl",
            correctOption = "C", explanation = "J.K. Rowling is the British author of the world-famous Harry Potter fantasy book series."
        ),
        Question(
            category = "GK", ageDivision = "JUNIORS",
            text = "How many keys are on a standard modern piano keyboard?",
            optionA = "64 keys", optionB = "76 keys", optionC = "88 keys", optionD = "100 keys",
            correctOption = "C", explanation = "A standard piano keyboard has 88 keys, consisting of 52 white keys and 36 black keys."
        ),
        Question(
            category = "GK", ageDivision = "JUNIORS",
            text = "What is the primary language spoken in Brazil?",
            optionA = "Spanish", optionB = "Portuguese", optionC = "English", optionD = "French",
            correctOption = "B", explanation = "Brazil is unique in South America as its official language is Portuguese, due to Portuguese colonization."
        ),
        Question(
            category = "GK", ageDivision = "JUNIORS",
            text = "Which device do we use to input text and type into a computer?",
            optionA = "Monitor", optionB = "Mouse", optionC = "Keyboard", optionD = "Printer",
            correctOption = "C", explanation = "A keyboard is the standard input hardware device used to type letters and commands."
        ),

        // --- SENIORS (Ages 13+) ---
        Question(
            category = "GK", ageDivision = "SENIORS",
            text = "In which country was the game of Chess invented?",
            optionA = "China", optionB = "India", optionC = "Russia", optionD = "Egypt",
            correctOption = "B", explanation = "Chess is believed to have originated in Northern India around the 6th century AD during the Gupta Empire as 'Chaturanga'."
        ),
        Question(
            category = "GK", ageDivision = "SENIORS",
            text = "Which international organization was founded in 1945 to maintain global peace and security?",
            optionA = "NATO", optionB = "World Health Organization", optionC = "United Nations", optionD = "European Union",
            correctOption = "C", explanation = "The United Nations (UN) was established after World War II to promote international co-operation."
        ),
        Question(
            category = "GK", ageDivision = "SENIORS",
            text = "What is the name of the longest river in the world, flowing through multiple northeastern African countries?",
            optionA = "Amazon River", optionB = "Mississippi River", optionC = "Nile River", optionD = "Yangtze River",
            correctOption = "C", explanation = "The Nile River is traditionally considered the longest river in the world, measuring about 6,650 kilometers (4,130 miles)."
        ),
        Question(
            category = "GK", ageDivision = "SENIORS",
            text = "Who painted the famous Renaissance portrait known as the Mona Lisa?",
            optionA = "Michelangelo", optionB = "Leonardo da Vinci", optionC = "Vincent van Gogh", optionD = "Pablo Picasso",
            correctOption = "B", explanation = "Leonardo da Vinci painted the Mona Lisa in Florence, Italy, during the early 16th century."
        ),
        Question(
            category = "GK", ageDivision = "SENIORS",
            text = "What is the capital city of Australia?",
            optionA = "Sydney", optionB = "Melbourne", optionC = "Canberra", optionD = "Brisbane",
            correctOption = "C", explanation = "Canberra was chosen as the capital city in 1908 as a compromise between rivals Sydney and Melbourne."
        )
    )
}
