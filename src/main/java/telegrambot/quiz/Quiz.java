package telegrambot.quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Quiz {

    private List<List<Question>> categories;

    public Quiz() {
        initCategories();
    }

    private List<Question> initQuestions() {
        // maybe read from json or xml file

        return List.of(
                new Question("Japan", "What is the name of the popular Japanese dish made from fermented soybeans?",
                        List.of("Sushi", "Ramen", "Miso", "Tempura"), "Miso"),

                new Question("Shakespeare", "In which famous Shakespearean play does the character Othello appear?",
                        List.of("Macbeth", "Romeo and Juliet", "Othello", "Hamlet"), "Othello"),

                new Question("Human Body", "What is the largest organ in the human body?",
                        List.of("Brain", "Liver", "Heart", "Skin"), "Skin"),

                new Question("Paintings", "Who painted the famous artwork \"Mona Lisa\"?",
                        List.of("Vincent van Gogh", "Pablo Picasso", "Leonardo da Vinci", "Michelangelo"), "Leonardo da Vinci"),

                new Question("Fawlty Towers", "What was the name of the Spanish waiter in the TV sitcom 'Fawlty Towers'?",
                        List.of("Manuel", "Pedro", "Alfonso", "Javier"), "Manuel"),

                new Question("War of the Roses", "Which battles took place between the Royal Houses of York and Lancaster?",
                        List.of("Thirty Years War", "Hundred Years War", "Wars of the Roses", "English Civil War"), "Wars of the Roses"),

                new Question("Thomas the Tank Engine", "Which former Beatle narrated the TV adventures of Thomas the Tank Engine?",
                        List.of("John Lennon", "Paul McCartney", "George Harrison", "Ringo Starr"), "Ringo Starr"),

                new Question("Queen Anne", "Queen Anne was the daughter of which English Monarch?",
                        List.of("James II", "Henry VIII", "Victoria", "William I"), "James II"),

                new Question("Rhapsody in Blue", "Who composed 'Rhapsody in Blue'?",
                        List.of("Irving Berlin", "George Gershwin", "Aaron Copland", "Cole Porter"), "George Gershwin"),

                new Question("Celsius Equivalent", "What is the Celsius equivalent of 77 degrees Fahrenheit?",
                        List.of("15", "20", "25", "30"), "25"),

                new Question("Suffolk Punch and Hackney", "What are Suffolk Punch and Hackney?",
                        List.of("Carriage", "Wrestling style", "Cocktail", "Horse"), "Horse"),

                new Question("Shakespeare Play", "Which Shakespeare play features the line 'Neither a borrower nor a lender be'?",
                        List.of("Hamlet", "Macbeth", "Othello", "The Merchant of Venice"), "Hamlet"),

                new Question("Largest City in the USA's Largest State", "Which is the largest city in the USA's largest state?",
                        List.of("Dallas", "Los Angeles", "New York", "Anchorage"), "Anchorage"),

                new Question("Meaning of 'Aristocracy'", "The word 'aristocracy' literally means power in the hands of",
                        List.of("The few", "The best", "The barons", "The rich"), "The best"),

                new Question("Wearing a 'Peruke'", "Where would a 'peruke' be worn?",
                        List.of("Around the neck", "On the head", "Around the waist", "On the wrist"), "On the head"),

                new Question("Queen Elizabeth I's Birthplace", "In which palace was Queen Elizabeth I born?",
                        List.of("Greenwich", "Richmond", "Hampton Court", "Kensington"), "Greenwich"),

                new Question("Origin of the Word 'Quark'", "From which author's work did scientists take the word 'quark'?",
                        List.of("Lewis Carroll", "Edward Lear", "James Joyce", "Aldous Huxley"), "James Joyce"),

                new Question("Island Ruled by Britain", "Which of these islands was ruled by Britain from 1815 until 1864?",
                        List.of("Crete", "Cyprus", "Corsica", "Corfu"), "Corfu"),

                new Question("Pirate", "In 1718, which pirate died in battle off the coast of what is now North Carolina?",
                        List.of("Calico Jack", "Blackbeard", "Bartholomew Roberts", "Captain Kidd"), "Blackbeard"),
        new Question("Toothed Whale", "Which of these cetaceans is classified as a “toothed whale”?" ,
                List.of("Gray whale","Minke whale", "Sperm whale", "Humpback whale"), "Sperm whale"));
    }

    private void initCategories() {
        this.categories = new ArrayList<>();

        // Question list for each category
        List<Question> category100 = new ArrayList<>();
        List<Question> category1000 = new ArrayList<>();
        List<Question> category16000 = new ArrayList<>();
        List<Question> category64000 = new ArrayList<>();
        List<Question> category1000000 = new ArrayList<>();

        // Populate the question lists with questions of different levels
        for (Question question : initQuestions()) {
            if (question.getTitle().equals("Japan") || question.getTitle().equals("Shakespeare")
                    || question.getTitle().equals("Human Body") || question.getTitle().equals("Paintings")
                    || question.getTitle().equals("Fawlty Towers")) {
                category100.add(question);

            } else if (question.getTitle().equals("War of the Roses") || question.getTitle().equals("Thomas the Tank Engine")
                    || question.getTitle().equals("Queen Anne") || question.getTitle().equals("Rhapsody in Blue")
                    || question.getTitle().equals("Celsius Equivalent")) {
                category1000.add(question);

            } else if (question.getTitle().equals("Suffolk Punch and Hackney") || question.getTitle().equals("Shakespeare Play")
                    || question.getTitle().equals("Largest City in the USA's Largest State") || question.getTitle().equals("Meaning of 'Aristocracy'")
                    || question.getTitle().equals("Wearing a 'Peruke'")) {
                category16000.add(question);

            } else if (question.getTitle().equals("Queen Elizabeth I's Birthplace") || question.getTitle().equals("Origin of the Word 'Quark'")
                    || question.getTitle().equals("Island Ruled by Britain")) {
                category64000.add(question);
                
            } else if (question.getTitle().equals("Pirate")
                    || question.getTitle().equals("Toothed Whale")) {
                category1000000.add(question);
            }
        }

        // Add question categories to the main list
        categories.add(category100);
        categories.add(category1000);
        categories.add(category16000);
        categories.add(category64000);
        categories.add(category1000000);
    }


    public Question getRandomQuestion(final int gameLevel) {
        Question response = null;

        if (gameLevel >= 0) {
            final List<Question> category = categories.get(gameLevel);
            Random r = new Random();
            int index = r.nextInt(category.size());
            response = category.get(index);

        } else {
            // Handle the case when the game level is out of range
        }

        return response;
    }
    
    public List<List<Question>> getCategories() {
        return categories;
    }
}

