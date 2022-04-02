package es.ulpgc.eite.da.quiz.question;

import android.content.Intent;
import android.util.Log;

import java.lang.ref.WeakReference;

import es.ulpgc.eite.da.quiz.app.AppMediator;
import es.ulpgc.eite.da.quiz.app.CheatToQuestionState;
import es.ulpgc.eite.da.quiz.app.QuestionToCheatState;

public class QuestionPresenter implements QuestionContract.Presenter {

  public static String TAG = QuestionPresenter.class.getSimpleName();

  private AppMediator mediator;
  private WeakReference<QuestionContract.View> view;
  private QuestionState state;
  private QuestionContract.Model model;

  public QuestionPresenter(AppMediator mediator) {
    this.mediator = mediator;
    state = mediator.getQuestionState();
  }

  @Override
  public void onStart() {
    Log.e(TAG, "onStart()");

    // call the model
    state.question = model.getQuestion();
    state.option1 = model.getOption1();
    state.option2 = model.getOption2();
    state.option3 = model.getOption3();

    // reset state to tests
    state.answerCheated=false;
    state.optionClicked = false;
    state.option = 0;

    // update the view
    disableNextButton();
    view.get().resetReply();
  }


  @Override
  public void onRestart() {
    Log.e(TAG, "onRestart()");
    //TODO: falta implementacion
    //Hay que actualizar el índice!!
    model.setQuizIndex(state.quizIndex);
    //Este método se ejecuta cuando hacemos
    // giro de pantalla,
    // después del onDestroy, por lo que es el que guarda
    //el estado
    //Si se ha pulsado en una opción
    if(state.optionClicked){
      //y la opción escogida es correcta
      if(model.isCorrectOption(state.option)){
        //La respuesta se actualiza y se mantiene en correcta
        view.get().updateReply(true);
      }else{
        view.get().updateReply(false);
      } //si no pulsamos en ninguna opción debería quedarse vacía
      //por lo que llamamos al resetReply
    }else{
      view.get().resetReply();
    }
  }


  @Override
  public void onResume() {
    Log.e(TAG, "onResume()");
    //TODO: falta implementacion
    // use passed state if is necessary
    CheatToQuestionState savedState = getStateFromCheatScreen();
    if (savedState != null) {
      state.answerCheated = savedState.answerCheated;
      if(state.answerCheated){
        if(model.hasQuizFinished()){
          state.optionEnabled = false;
          state.nextEnabled = false;
        }else{
          onNextButtonClicked();
        }

      }
      // fetch the model
    }
    // update the view
    view.get().displayQuestion(state);
  }


  @Override
  public void onDestroy() {
    Log.e(TAG, "onDestroy()");
  }

  @Override
  public void onOptionButtonClicked(int option) {
    Log.e(TAG, "onOptionButtonClicked()");

    //TODO: falta implementacion
    state.optionClicked = true;
    boolean isCorrect = model.isCorrectOption(option);
    if(option == 1){
      state.option = 1;
    }else if(option == 2){
      state.option = 2;
    }else{
      state.option = 3;
    }
    //El único botón que depende de si ha respondido
    //correcta o incorrectamente es el Cheat
    if(isCorrect) {
      state.cheatEnabled=false;
    } else {
      state.cheatEnabled=true;
    }
      this.enableNextButton();
    //Hay que actualizar la respuesta
    //en base a si es correcta o incorrecta
    view.get().updateReply(isCorrect);
    view.get().displayQuestion(state);

  }

  @Override
  public void onNextButtonClicked() {
    Log.e(TAG, "onNextButtonClicked()");
    //TODO: falta implementacion
    //Actualizo el índice
    model.updateQuizIndex();
    //Cojo la pregunta correspondiente con sus respuestas
    state.question = model.getQuestion();
    state.option1 = model.getOption1();
    state.option2 = model.getOption2();
    state.option3 = model.getOption3();

    state.optionClicked = false;
    view.get().resetReply();
    disableNextButton();
    view.get().displayQuestion(state);

  }
  @Override
  public void onCheatButtonClicked() {
    Log.e(TAG, "onCheatButtonClicked()");
    //TODO: falta implementacion
    String pasaCheat = model.getAnswer();
    QuestionToCheatState newState = new QuestionToCheatState(pasaCheat);
    passStateToCheatScreen(newState);
    view.get().navigateToCheatScreen();

  }

  private void passStateToCheatScreen(QuestionToCheatState state) {
    //TODO: falta implementacion
    mediator.setQuestionToCheatState(state);


  }

  private CheatToQuestionState getStateFromCheatScreen() {
    //TODO: falta implementacion
    return mediator.getCheatToQuestionState();
  }

  private void disableNextButton() {
    state.optionEnabled=true;
    state.cheatEnabled=true;
    state.nextEnabled=false;

  }

  private void enableNextButton() {
    state.optionEnabled=false;
    //state.cheatEnabled = false;

    if(!model.hasQuizFinished()) {
      state.nextEnabled=true;
    }
  }

  @Override
  public void injectView(WeakReference<QuestionContract.View> view) {
    this.view = view;
  }

  @Override
  public void injectModel(QuestionContract.Model model) {
    this.model = model;
  }

}
