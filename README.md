![icon.png](icon.png)
___
# APLIKACJA HEALTHY EYES
Celem stworzenia aplikacji mobilnej na system operacyjny typu Android "Healthy Eyes", jest wspomaganie ludzi w kontroli stanu ich oczu, poprzez wykonywanie podstawowych testów tj.: test Snellena, Ishihary czy test Amslera służące do wykrywania  wad wzroku oraz chorób – jaskry i daltonizmu.
***
## FUNKCJONALNOŚCI APLIKACJI
### REJESTRACJA NOWYCH UŻYTKOWNIKÓW z wykorzystaniem usługi Firebase Authentication
Rejestracja nowego użytkownika do aplikacji odbywa się poprzez odpowiedzenie na 4 pytania. Jeżeli na co najmniej połowę pytań odpowiedź jest twierdząca, wtedy od razu następuje przekierowanie użytkownika do formularza rejestracji. Jeśli nie, on sam podejmuje decyzję czy chcę skorzystać z aplikacji czy też nie. Formularz rejestracyjny w pierwszej części  składa się z pól do wpisania imienia, nazwiska, roku urodzenia, płci oraz wymaga podania informacji o ewentualnych istniejących już wadach wzroku u pacjenta. Po wypełnieniu wszystkich powyżej zdefiniowanych pól użytkownik klika ZAREJESTRUJ SIĘ i jest przenoszony do drugiej części rejestracji, w której podaje dane takie jak: email i hasło, przy czym wymaga się powtórzenia hasła w celu zapisania odpowiedniego w bazie danych. Po wpisaniu tych informacji klika przycisk ZAŁÓŻ KONTO i zostaje przekierowany do strony logowania.
### LOGOWANIE DO APLIKACJI
Zarejestrowany użytkownik ma możliwość zalogowania się do aplikacji poprzez poprawne wpisanie emaila i hasła podanych w procesie rejestracji. Po kliknięciu ZALOGUJ SIĘ, gdy się wszystko będzie zgadzać, zostanie przeniesiony do głównego okna aplikacji.
### BAZA DANYCH Firestore
Po prawidłowym przebiegu procesu rejestracji dane użytkownika tj.: jego imię, nazwisko, rok urodzenia, informacja o wadzie wzroku jaką może posiadać, email i zahaszowane hasło zapisane zostają w bazie danych. Dodatkowo przechowywane są w niej także wyniki testów, które wykonuje pacjent. Każdy zakończony test zostaje zapisany w bazie w postaci: daty, godziny, unikalnego identyfikatora pacjenta oraz uzyskanego wyniku odpowiedniego dla obu oczu lub dla każdej gałki ocznej oddzielnie. Ponadto użytkownik, który nie chce już korzystać z aplikacji może usunąć swoje konto, wykorzystując w tym celu przycisk „USUŃ KONTO” znajdujący się w layoucie ustawień. Jeżeli chodzi o wyniki, tego użytkownika to pozostają one nienaruszone tak, aby stanowiły archiwum, gdy zechcę on wrócić po pewnym czasie z powrotem i będzie mógł sprawdzić, czy jego wzrok utrzymuje się w dalszym ciągu na takim poziomie jak kiedyś.
### STATYSTYKI

### USTAWIENIA
W oknie ustawień użytkownik ma możliwość dokonania zmiany hasła lub informacji o wadzie wzroku, jakby w czasie korzystania z aplikacji taka się pojawiła. Dodatkowo ma on również opcję usunięcia swojego konta (przycisk USUŃ KONTO), jeżeli stwierdzi, że nie potrzebuje korzystać dłużej z takiego udogodnienia.
### WYLOGOWANIE Z APLIKACJI
W oknie głównym aplikacji po kliknięciu przycisku "WYLOGUJ" użytkownik zostaje prawidłowo wylogowany z aplikacji i przeniesiony do strony startowej, która pozwala mu na ponowne zalogowanie lub rejestrację do aplikacji.
