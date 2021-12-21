import pandas as pd
import sys


ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"


class PlugLead:
    def __init__(self, lead_characters):
        if len(lead_characters) != 2 or type(lead_characters).__name__ != "str":
            raise ValueError
        else:
            self.first_character = lead_characters[0]
            self.second_character = lead_characters[1]

    def encode(self, character):
        if character != self.first_character and character != self.second_character:
            return character
        elif character == self.first_character:
            return self.second_character
        elif character == self.second_character:
            return self.first_character


class Plugboard:
    def __init__(self):
        self.lead_dict = dict()

    def add(self, plug_lead):
        if len(self.lead_dict) < 20:
            first_character = plug_lead.first_character
            second_character = plug_lead.second_character
            self.lead_dict[first_character] = second_character
            self.lead_dict[second_character] = first_character

    def encode(self, character):
        if character in self.lead_dict:
            return self.lead_dict[character]
        else:
            return character


class Rotor:
    def __init__(self, pattern, rotation="A", ring=1, notch=None):
        notch_dict = {"I": "Q", "II": "E", "III": "V", "IV": "J", "V": "Z"}
        self.pattern = pattern
        self.rotation = rotation
        self.ring = ring - 1

        if pattern in notch_dict:
            notch = notch_dict[pattern]

        self.notch = notch
        self.alphabet = ALPHABET[ALPHABET.find(self.rotation) - self.ring:] + ALPHABET[:ALPHABET.find(self.rotation) - self.ring]

        self.columns = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
                        "R", "S", "T", "U", "V", "W", "X", "Y", "Z"]
        self.index = ["Beta", "Gamma", "I", "II", "III", "IV", "V", "A", "B", "C"]
        self.data = ["L	E	Y	J	V	C	N	I	X	W	P	B	Q	M	D	R	T	A	K	Z	G	F	U	H	O	S",
                     "F	S	O	K	A	N	U	E	R	H	M	B	T	I	Y	C	W	L	Q	P	Z	X	V	G	J	D",
                     "E	K	M	F	L	G	D	Q	V	Z	N	T	O	W	Y	H	X	U	S	P	A	I	B	R	C	J",
                     "A	J	D	K	S	I	R	U	X	B	L	H	W	T	M	C	Q	G	Z	N	P	Y	F	V	O	E",
                     "B	D	F	H	J	L	C	P	R	T	X	V	Z	N	Y	E	I	W	G	A	K	M	U	S	Q	O",
                     "E	S	O	V	P	Z	J	A	Y	Q	U	I	R	H	X	L	N	F	T	G	K	D	C	M	W	B",
                     "V	Z	B	R	G	I	T	Y	U	P	S	D	N	H	L	X	A	W	M	J	Q	O	F	E	C	K",
                     "E	J	M	Z	A	L	Y	X	V	B	W	F	C	R	Q	U	O	N	T	S	P	I	K	H	G	D",
                     "Y	R	U	H	Q	S	L	D	P	X	N	G	O	K	M	I	E	B	F	Z	C	W	V	J	A	T",
                     "F	V	P	J	I	A	O	Y	E	D	R	Z	X	W	G	C	T	K	U	Q	S	B	N	M	H	L"]

        self.preprocess_data()
        self.dframe = pd.DataFrame(index=self.index, columns=self.columns, data=self.data)
        self.dframe.index.name = "Label"

    def preprocess_data(self):
        for idx in range(len(self.data)):
            self.data[idx] = self.data[idx].split("\t")

    def encode_right_to_left(self, letter):
        idx = ALPHABET.find(letter)
        rotor_letter = self.alphabet[idx]
        encoded_letter = self.dframe[rotor_letter][self.pattern]
        returning_idx = self.alphabet.find(encoded_letter)

        return ALPHABET[returning_idx]

    def encode_left_to_right(self, letter):
        idx = ALPHABET.find(letter)
        rotor_letter = self.alphabet[idx]

        for column in self.columns:
            if self.dframe.loc[self.pattern][column] == rotor_letter:
                returning_idx = self.alphabet.find(column)

                return ALPHABET[returning_idx]

    def update_alphabet(self):
        self.alphabet = ALPHABET[ALPHABET.find(self.rotation) - self.ring:] + ALPHABET[:ALPHABET.find(self.rotation) - self.ring]


class Enigma:
    def __init__(self, rotor_list, reflector=None, plug_board=None):
        self.rotor_list = rotor_list
        self.reflector = reflector
        self.plug_board = plug_board

    def encode(self, letter):
        self.turnover()

        if self.plug_board is not None:
            letter = self.plug_board.encode(letter)

        for rotor in self.rotor_list:
            encoded_letter = rotor.encode_right_to_left(letter)
            letter = encoded_letter

        encoded_letter = self.reflector.encode_right_to_left(letter)
        self.rotor_list.reverse()

        for rotor in self.rotor_list:
            idx = ALPHABET.find(encoded_letter)
            encoded_letter = ALPHABET[idx]
            encoded_letter = rotor.encode_left_to_right(encoded_letter)

        self.rotor_list.reverse()

        if self.plug_board is not None:
            return self.plug_board.encode(encoded_letter)
        return encoded_letter

    def encode_message(self, message):
        returning_message = ""

        for char in message:
            returning_message += self.encode(char)

        return returning_message

    def turnover(self):
        rotor1 = self.rotor_list[0]
        rotor2 = self.rotor_list[1]
        rotor3 = self.rotor_list[2]

        pos1 = rotor1.rotation
        pos2 = rotor2.rotation
        pos3 = rotor3.rotation

        notch1 = rotor1.notch
        notch2 = rotor2.notch

        idx1 = ALPHABET.find(pos1)
        idx1 += 1

        if idx1 == 26:
            idx1 = 0

        rotor1.rotation = ALPHABET[idx1]
        rotor1.update_alphabet()

        if pos1 == notch1 or pos2 == notch2:
            idx2 = ALPHABET.find(pos2)
            idx2 += 1

            if idx2 == 26:
                idx2 = 0

            rotor2.rotation = ALPHABET[idx2]
            rotor2.update_alphabet()

        if pos2 == notch2:
            idx3 = ALPHABET.find(pos3)
            idx3 += 1

            if idx3 == 26:
                idx3 = 0

            rotor3.rotation = ALPHABET[idx3]
            rotor3.update_alphabet()


args = sys.argv

rotor1 = Rotor(pattern=args[1], rotation=args[2], ring=int(args[3]))
rotor2 = Rotor(pattern=args[4], rotation=args[5], ring=int(args[6]))
rotor3 = Rotor(pattern=args[7], rotation=args[8], ring=int(args[9]))

rotor_list = [rotor1, rotor2, rotor3]

reflector = Rotor(args[10])

enigma = Enigma(rotor_list, reflector)

print(enigma.encode_message(args[11]))
