package automaton.model

case class State(label: String) {
  override def toString: String = label
}

case class Computation(str: String,
                       isAccepted: Boolean = false,
                       computed: Boolean = false,
                       trace: String = "")


trait Automaton[R <: Rule, A <: Automaton[R, A]] {
  def states:Set[State]
  def alphabet:Set[String]
  def transitions:Set[R]
  def initialState:State
  def finalStates:Set[State]
  def computations:Seq[Computation] = Seq.empty

  def withUpdatedComputations(newComps : Seq[Computation]): A

  def testString(input: String): (Boolean, String)

  def testAutomaton(): A =
    val updated = computations.map { c =>
      val (result, trace) = testString(c.str)
      c.copy(isAccepted = result, computed = true, trace = trace)
    }
    withUpdatedComputations(updated)

  protected def validate ():Unit =
      require(states.nonEmpty, "Automaton must have at least one state")
      require(alphabet.nonEmpty, "Alphabet must not be empty")
      require(areDisjoint(states, alphabet), "States and alphabet must be disjoint")
      require(states.contains(initialState), "Initial state must be in states")
      require(finalStates.forall(states.contains), "All final states must be in states")

  private def areDisjoint (states:Set[State], strings:Set[String]):Boolean =
    !states.exists(s => strings.contains(s.label))
}

