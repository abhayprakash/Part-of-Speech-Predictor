<b>PROBLEM STATEMENT:</b>
In this era of supercomputers, people communicate through text messages, there is a tendency to forget about grammar and punctuation while texting. But sometimes the reader is not able to understand such messages. Therefore, to help those readers you have to write a program to amend a small and specific general mistake, i.e. to choose appropriate word between "your" and "you're". 

"Your" is a possessive pronoun as in "your car" or "your book". 
"You're" is a contraction of "you are" as in "You're wrong when you use your but actually mean 'you are'". 

You are given sentences containing "you're" or "your" at one or more places. Four asterisks "****" have been put at those places. You have to make correct sentences by replacing "****" with appropriate choice from the two words.


<b>NOTE:</b>
The code is written to predict POS one among {Possive Pronoun - PRP$ and Pronoun with verb - PRP_VRB} as the task was to fill blanks in given sentence from one of the two words - {"your" or "you're"}. This code can be used to predict any of the POS besides PRP$ and PRP_VBP too.

<b>EXTERNAL LIBRARY:</b>
Stanford core NLP library - POS tagger has been used for finding POS for each word.
